package com.fileops.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fileops.service.DropboxServiceImpl;
import com.fileops.service.FileOpsService;
import com.fileops.utils.FileResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class FileOpsController {

    @Autowired
    FileOpsService fileOpsService;

    @Autowired
    private DropboxServiceImpl dropboxService;

    @PostMapping("/uploadFileToLocalDisk")
    public  ResponseEntity uploadFiletoLocal (@RequestParam("file") MultipartFile file) {

        String fileName = fileOpsService.uploadFile(file);

        String fileUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/download/")
                .path(fileName)
                .toUriString();

        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(fileName,fileUri,"File uploaded with success!"));
    }

    @GetMapping("/downloadFileFromLocalDisk/{filename:.+}")
    public ResponseEntity downloadFile(@PathVariable String filename) {

        Resource resource = fileOpsService.downloadFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @PostMapping("/createGitRepo/{repoName}")
    public ResponseEntity createGitRepo(@PathVariable String repoName) {

        String json = "{\"name\": \"" + repoName + "\", \"public\": \""
                + true + "\", \"auto_init\": " + true + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        template.exchange("https://api.github.com/user/repos", HttpMethod.POST, request, Object.class);

        return ResponseEntity.ok()
                .body(repoName+" repo created successfully");

    }

    @PutMapping("/uploadFileToGit")
    public ResponseEntity uploadFileToGit(@RequestParam("file") MultipartFile file) throws IOException {

        /**approach1
      //  File convFile = new File( file.getOriginalFilename());
      //  file.transferTo(convFile);
      //  FileInputStream fileInputStreamReader = new FileInputStream(convFile);
      //  byte[] bytes = new byte[(int)convFile.length()];
      //  fileInputStreamReader.read(bytes);
      //  String encodedString = java.util.Base64.getEncoder().encodeToString(bytes);*/

        /**approach3
         // InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("new_file.txt");
         // String fileToAdd =  new String(Objects.requireNonNull(fileStream).readAllBytes(), StandardCharsets.UTF_8);
         // var encodedContent = java.util.Base64.getEncoder().encodeToString(fileToAdd.getBytes()); * */

        /***approach4 common codec
         // String originalInput = "test input";
         // Base64 base64 = new Base64();
         // String encodedString = new String(base64.encode(originalInput.getBytes()));*/

        String origfileName=file.getOriginalFilename();
        Path path = fileOpsService.uploadFilePath(file);

        byte[] fileContent = Files.readAllBytes(path);
        String encodedString = java.util.Base64.getEncoder().encodeToString(fileContent);
        //String encodedString = java.util.Base64.getMimeEncoder().encodeToString("hellow".getBytes());
        //String encodedString ="bXkgbmV3IGZpbGUgY29udGVudHM=";
        //String encodedString = java.util.Base64.getEncoder().encodeToString("hello".getBytes());


        String json = "{\"message\": \"my commit message\", \"content\": \""+encodedString+"\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        /**https://api.github.com/repos/OWNER/REPO/contents/PATH*/
        template.exchange("https://api.github.com/repos/shajnack/testops/contents/"+origfileName, HttpMethod.PUT, request, Object.class);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + origfileName+ "\"")
                .body("file uploaded to git successfully");

    }


    /**curl \
    //  -H "Accept: application/vnd.github.v3+json" \
    //  -H "Authorization: token <TOKEN>" \
    //  https://api.github.com/repos/OWNER/REPO/contents/PATH*/


    @GetMapping("/downloadFileFromGit/{filename:.+}")
    public ResponseEntity downloadFileFromGit(@PathVariable String filename) throws IOException{

        String json="";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        //headers.set("Accept",  "application/vnd.github.VERSION.raw");
        headers.set("Authorization", "token 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");

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        /**https://api.github.com/repos/OWNER/REPO/contents/PATH*/
        ResponseEntity<JsonNode> repos = template.exchange("https://api.github.com/repos/shajnack/testops/contents/"+filename,
                HttpMethod.GET, request, JsonNode.class);

        String contentStr= repos.getBody().get("content").toString();
        System.out.println(contentStr);


        //byte[] decodedBytes = java.util.Base64.getDecoder().decode(contentStr);
        //String decodedString = new String(decodedBytes,"UTF-8");
        //return decodedString;

        //byte[] decodedBytes = java.util.Base64.getMimeDecoder().decode(contentStr.trim());
       // String decodedMime = new String(decodedBytes,"UTF-8");
       //return decodedMime;

        //byte[] decodedImg = java.util.Base64.getMimeDecoder().decode(contentStr.getBytes(StandardCharsets.UTF_8));
        byte[] originalValue = StringUtils.getBytesUtf8(contentStr);
        Base64 decoder = new Base64(true);
        byte[] decodedByte= decoder.decode(originalValue);

        Path destinationFile = Paths.get("/Users/aftabkarim/Desktop/shajna/FileOpsSRC/fileoptest", filename);
        Files.write(destinationFile, decodedByte);

        return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename+ "\"")
             .body(decodedByte);


    }

    @GetMapping("/downloadFileFromDropbox/")
    public ResponseEntity downloadFileFromDropbox(@RequestParam String filepath) throws DbxException, IOException {

        //String filepath="/test/"+fileName;
        Path inputPath = Paths.get(filepath);
        String filename= String.valueOf(inputPath.getFileName());

        InputStream inputStream = dropboxService.downloadFile(filepath.toString());

        File targetFile = new File("/Users/aftabkarim/Desktop/shajna/FileOpsTest/"+filename);
        org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, targetFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filepath+ "\"")
                .body("downloaded from dropbox");


    }




    @GetMapping("/test")
    public String test() {

        return "<html>hi</html>";

    }

    @GetMapping("/downloadAsFile/{filename:.+}")
    public ResponseEntity<Resource> downloadfilefromlocalAsFile() throws IOException {

        File file = new File("/Users/aftabkarim/Desktop/shajna/FileOpsSRC/fileoptest/index.js");

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=index.js");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    /**get repo detail of user*/
    @GetMapping("/gitGetRepoDetail")
    public String gitGetRepoDetail() {

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        ResponseEntity<JsonNode> repos = restTemplate
                .getForEntity("https://api.github.com/users/shajnack/repos", JsonNode.class);
        int counter = 1;
        StringBuilder result = new StringBuilder("\n List of Repositories");
        if (repos.getBody().isArray()) {
            for(JsonNode jsonNode : repos.getBody()) {
                result.append("\n Repo ").append(counter++).append("::");
                result.append(jsonNode.get("name").asText());
            }
        }
        return result.toString();
    }


    //curl -i -H "Authorization: token ghp_16C7e42F292c6912E7710c838347Ae178B4a" \
    //      -d '{
    //     "name": "blog",
    //     "auto_init": true,
    //     "private": true,
    //     "gitignore_template": "nanoc"
//}' \
    //  https://api.github.com/user/repos

   /* @PostMapping("/gitCreateRepo") initial version
    public void gitCreateRepo() {
        String url = "https://api.github.com/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "testReposs");
        params.put("public", "true");
        params.put("auto_init", "true");
        params.put("gitignore_template", "nanoc");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Request Successful");
            System.out.println(response.getBody());
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

    }*/
    //curl -H "Authorization: token ACCESS_TOKEN" --data '{"name":"NEW_REPO_NAME"}' https://api.github.com/user/repos


/**download full content.hardcoded version*/
    @GetMapping("/downloadFileFullDataFromGit")
    public String downloadFileFullDataFromGit() {

        String json = "";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");


        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        //https://api.github.com/repos/OWNER/REPO/contents/PATH
        ResponseEntity<String> repos = template.exchange("https://api.github.com/repos/shajnack/testops/contents/hello1.txt",
                HttpMethod.GET, request, String.class);



        return repos.toString();


    }
    @GetMapping("/downloadFileContentFromGitToFile")
    public ResponseEntity downloadFileContentFromGitToFile() throws IOException {

        String json = "";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");

        headers.set("Authorization", "token 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");

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        //https://api.github.com/repos/OWNER/REPO/contents/PATH
        ResponseEntity<JsonNode> repos = template.exchange("https://api.github.com/repos/shajnack/testops/contents/hello1.txt",
                HttpMethod.GET, request, JsonNode.class);

        /**below snippet works.Commented as the same data is availble via string in above API*/
        ByteArrayResource resource = new ByteArrayResource(repos.getBody().get("content").toString().getBytes());
        HttpHeaders headerss = new HttpHeaders();
        headerss.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headerss.add("Pragma", "no-cache");
        headerss.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(headerss)
                .contentLength(repos.getBody().get("content").toString().length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        /**section end*/

        /**byte[] decodedBytes = Base64.getDecoder().decode(repos.getBody().get("content").toString());
         String decodedString = new String(decodedBytes);

         File file = new File("/Users/aftabkarim/Desktop/shajna/FileOpsSRC/fileoptest/new1.txt");
         FileWriter wr = new FileWriter(decodedString);

         wr.write("hello");
         wr.flush();
         wr.close();
         return file;*/
    }

}