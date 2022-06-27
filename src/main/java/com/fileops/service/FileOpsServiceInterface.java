package com.fileops.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileOpsServiceInterface {

    void init();
    String uploadFile(MultipartFile file);
    Resource downloadFile(String fileName);

}

