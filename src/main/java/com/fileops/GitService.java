package com.fileops;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitService {

    private final RestTemplate restTemplate;

    @Autowired
    public GitService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callGitRepoCreate(){

        return restTemplate.getForObject("",String.class);
    }
}
