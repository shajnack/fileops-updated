package com.fileops.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.annotation.PostConstruct;

import com.fileops.config.FileUploadProperties;
import com.fileops.exception.FileProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileOpsServiceImpl implements FileOpsService {

    private final Path uploadTempDirLocation;
    private final Path downloadDirLocation;

    @Autowired
    public FileOpsServiceImpl(FileUploadProperties fileUploadProperties) {

        this.uploadTempDirLocation = Paths.get(fileUploadProperties.getUploadTempLocation())
                .toAbsolutePath()
                .normalize();

        this.downloadDirLocation = Paths.get(fileUploadProperties.getDownloadLocation())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    @PostConstruct
    public void init() {

        try {
            Files.createDirectories(this.uploadTempDirLocation);
            Files.createDirectories(this.downloadDirLocation);
        }
        catch (Exception ex) {
            //System.out.println("err");
            throw new FileProcessingException("Could not initialize directories!");
        }
    }

    @Override
    public Path uploadFilePath(MultipartFile file) {
        Path dfile = null;
        try {
             String fileName = file.getOriginalFilename();
             dfile = this.uploadTempDirLocation.resolve(fileName);
             Files.copy(file.getInputStream(), dfile,StandardCopyOption.REPLACE_EXISTING);
             //return dfile;

        } catch (Exception e) {
           // System.out.println("err");
            throw new FileProcessingException("Could not upload!");
        }
        return dfile;
    }

    public Path getDownloadDirLocation() {

        return downloadDirLocation;
    }
    /** @Override
     public Resource downloadFile(String fileName) {
         Resource resource=null;
         try {
             Path file = this.dirLocation.resolve(fileName).normalize();
              resource = new UrlResource(file.toUri());

             if (resource.exists() || resource.isReadable()) {
                 return resource;
             }
             else {
                 System.out.println("err");
                 //throw new FileNotFoundException("Could not find file");
             }
         }
         catch (MalformedURLException e) {
             System.out.println("err");
            // throw new FileNotFoundException("Could not download file");
         }
         return resource;


 }*/
}
