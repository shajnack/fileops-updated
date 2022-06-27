package com.fileops.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fileops.service.FileOpsService;
import com.fileops.utils.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class FileOpsController {

    @Autowired
    FileOpsService fileOpsService;

    @PostMapping("/uploadfile")
    public  ResponseEntity uploadSingleFile (@RequestParam("file") MultipartFile file) {
        String upfile = fileOpsService.uploadFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/download/")
                .path(upfile)
                .toUriString();

        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(upfile,fileDownloadUri,"File uploaded with success!"));
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity downloadFile(@PathVariable String filename) {

        Resource resource = fileOpsService.downloadFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/test")
    public String test() {

       // Resource resource = fileOpsService.loadFile(filename);

        return "<html>hi</html>";


    }

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

   /* @PostMapping("/gitCreateRepo")
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
        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        // check response
        if (response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Request Successful");
            System.out.println(response.getBody());
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

    }*/
    //curl -H "Authorization: token ACCESS_TOKEN" --data '{"name":"NEW_REPO_NAME"}' https://api.github.com/user/repos


    @PostMapping("/gitCreateRepos")
    public void gitCreateRepos() {

        String json = "{\"name\": \"" + "testrepos6" + "\", \"public\": \""
                + true + "\", \"auto_init\": " + true + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        //headers.set("username",  "shajnack@gmail.com");
        //headers.set("password",  "O*oooooo22");
        headers.set("Authorization", "token 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");

        // ignore result because just testing connectivity
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        template.exchange("https://api.github.com/user/repos", HttpMethod.POST, request, Object.class);


    }




    @PutMapping("/uploadFiletoGit")
    public void uploadFiletoGit() {


        //'{"message":"my commit message","committer":{"name":"Monalisa Octocat","email":"octocat@github.com"},"content":"bXkgbmV3IGZpbGUgY29udGVudHM="}'
        String json = "{\"message\": \"my commit message\", \"content\": \"bXkgbmV3IGZpbGUgY29udGVudHM=\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");

        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        //https://api.github.com/repos/OWNER/REPO/contents/PATH
        template.exchange("https://api.github.com/repos/shajnack/testrepo/contents/hello1.txt", HttpMethod.PUT, request, Object.class);


    }

    //curl \
    //  -H "Accept: application/vnd.github.v3+json" \
    //  -H "Authorization: token <TOKEN>" \
    //  https://api.github.com/repos/OWNER/REPO/contents/PATH

    @GetMapping("/downloadFileFromGit")
    public String downloadFileFromGit() {


        String json = "";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");


        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        //https://api.github.com/repos/OWNER/REPO/contents/PATH
        ResponseEntity<String> repos = template.exchange("https://api.github.com/repos/shajnack/testrepo/contents/hello.txt",
                HttpMethod.GET, request, String.class);



        return repos.toString();


    }

    @GetMapping("/downloadFileContentFromGit")
    public String downloadFileContentFromGit() {


        String json = "";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept",  "application/vnd.github.v3+json");
        headers.set("Authorization", "token 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");


        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        RestTemplate template = new RestTemplate();
        //https://api.github.com/repos/OWNER/REPO/contents/PATH
        ResponseEntity<JsonNode> repos = template.exchange("https://api.github.com/repos/shajnack/testrepo/contents/hello.txt",
                HttpMethod.GET, request, JsonNode.class);

        int counter = 1;
        StringBuilder result = new StringBuilder("\ncontent is");
        if (repos.getBody().isArray()) {
            for(JsonNode jsonNode : repos.getBody()) {

                result.append(jsonNode.get("content").asText());
            }
        }
        return result.toString();



    }

}