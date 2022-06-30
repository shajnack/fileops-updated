package com.fileops.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileOpsService {

    void init();
    Path uploadFilePath(MultipartFile file);

    // Resource downloadFile(String fileName);

}

