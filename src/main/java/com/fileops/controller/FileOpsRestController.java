package com.fileops.controller;

import com.dropbox.core.DbxException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fileops.service.DropboxServiceImpl;
import com.fileops.service.FileOpsService;
import com.fileops.utils.FileResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api")
public class FileOpsRestController {

    @Autowired
    FileOpsService fileOpsService;
    @Autowired
    private DropboxServiceImpl dropboxServiceImpl;

    private final String gitAuthToken="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";

    @PostMapping("/createGitRepo/{repoName}")
    public ResponseEntity createGitRepo(@PathVariable String repoName) {

        String json = "{\"name\": \"" + repoName + "\", \"public\": \""
                + true + "\", \"auto_init\": " + true + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token "+gitAuthToken); /**Need to make it to runtime param*/

        HttpEntity<String> request = new HttpEntity<String>(json, headers);

        RestTemplate template = new RestTemplate();
        template.exchange("https://api.github.com/user/repos", HttpMethod.POST, request, Object.class);

        return ResponseEntity.ok()
                .body(repoName+" repo created successfully");

    }

    @PostMapping("/uploadFileToGit")
    public ResponseEntity uploadFileToGit(@RequestParam("file") MultipartFile file,@RequestParam(required = true) String gitAuthToken) throws IOException {

        //file.getBytes();
        String origFileName=file.getOriginalFilename();
        Path path = fileOpsService.uploadFilePath(file);

       // byte[] fileContent = Files.readAllBytes(path);
        byte[] fileContent = file.getBytes();
        String encodedString = java.util.Base64.getEncoder().encodeToString(fileContent);

        String json = "{\"message\": \"my commit message\", \"content\": \""+encodedString+"\"}"; /**Need to make it to runtime param*/

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token "+gitAuthToken); /**Need to make it to runtime param*/

        HttpEntity<String> request = new HttpEntity<String>(json, headers);

        RestTemplate template = new RestTemplate();
        /**https://api.github.com/repos/OWNER/REPO/contents/PATH*/
        template.exchange("https://api.github.com/repos/shajnack/testops/contents/"+origFileName,
                HttpMethod.PUT, request, Object.class);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + origFileName+ "\"")
                .body(origFileName+"file uploaded to git successfully to Repo /shajnack/testops/contents/");

    }

    @GetMapping("/downloadFileFromGit/{filename:.+}")
    public ResponseEntity downloadFileFromGit(@PathVariable String filename) throws IOException{

        String json="";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token "+gitAuthToken);/**Need to make it to runtime param*/

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        /**https://api.github.com/repos/OWNER/REPO/contents/PATH*/
        ResponseEntity<JsonNode> repos = template.exchange("https://api.github.com/repos/shajnack/testops/contents/"+filename,
                HttpMethod.GET, request, JsonNode.class); /**need to make the repo path configurable*/

        String contentStr= repos.getBody().get("content").toString();
        System.out.println(contentStr);

        byte[] originalValue = StringUtils.getBytesUtf8(contentStr);
        Base64 decoder = new Base64(true);
        byte[] decodedByte= decoder.decode(originalValue);

        Path destinationFile = Paths.get("/Users/shajnack/FileOpsTest/", filename);/**need to make the download path configurable*/
        Files.write(destinationFile, decodedByte);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename+ "\"")
                .header("downloadcheck","1")  /**can set the output path here as well*/
                .body("File downloaded from dropbox to the path /Users/shajnack/FileOpsTest/ "+decodedByte);
    }

    /**filepath from dropbox eg: /F1/new.txt*/
    @GetMapping("/downloadFileFromDropbox/")
    public ResponseEntity downloadFileFromDropbox(@RequestParam String filepath) throws DbxException, IOException {

        //String filepath="/test/"+fileName;
        Path inputPath = Paths.get(filepath);
        String filename= String.valueOf(inputPath.getFileName());

        InputStream inputStream = dropboxServiceImpl.downloadFile(filepath.toString());

        File targetFile = new File("/Users/shajnack/FileOpsTest/"+filename); /**need to make the  path configurable*/
        org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, targetFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filepath+ "\"")
                .body("File downloaded from dropbox to the path "+targetFile);

    }

    @PostMapping("/uploadFileToLocalDisk")
    public  ResponseEntity uploadFileToLocal(@RequestParam("file") MultipartFile file) {

        String fileName= file.getOriginalFilename();
        Path filePath = fileOpsService.uploadFilePath(file);
        String fileDir=filePath.toString();

        String fileUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(fileDir)
                .path(fileName)
                .toUriString();

        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(fileName,fileUri,"File uploaded successfully!"));
    }


    @GetMapping("/test")
    public String test() {
        return "Hi";

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

   /** @GetMapping("/downloadFileFromLocalDisk/{filename:.+}")
    public ResponseEntity downloadFile(@PathVariable String filename) {

        Resource resource = fileOpsService.downloadFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }*/
}