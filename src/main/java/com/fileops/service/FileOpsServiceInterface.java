package com.fileops.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileOpsServiceInterface {

    void init();
    Path uploadFilePath(MultipartFile file);
   // Resource downloadFile(String fileName);

}

