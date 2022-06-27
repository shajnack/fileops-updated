package com.fileops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitRestController {

    private final GitService gitService;

    public GitRestController(GitService gitService){

        this.gitService = gitService;
    }




}
