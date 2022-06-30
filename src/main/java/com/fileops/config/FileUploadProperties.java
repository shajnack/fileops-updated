package com.fileops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.ops")
public class FileUploadProperties {
    private String uploadTempLocation;
    private String downloadLocation;
    public String getUploadTempLocation() {
        return uploadTempLocation;
    }
    public void setUploadTempLocation(String uploadTempLocation) {
        this.uploadTempLocation = uploadTempLocation;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }
    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }
}


