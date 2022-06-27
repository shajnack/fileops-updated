package com.fileops.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.annotation.PostConstruct;

import com.fileops.config.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileOpsService implements FileOpsServiceInterface{

    private final Path dirLocation;

    @Autowired
    public FileOpsService(FileUploadProperties fileUploadProperties) {
        this.dirLocation = Paths.get(fileUploadProperties.getLocation())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    @PostConstruct
    public void init() {

        try {
            Files.createDirectories(this.dirLocation);
        }
        catch (Exception ex) {
            System.out.println("err");
            //throw new FileStorageException("Could not create upload dir!");
        }
    }



    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = null;
        try {
             fileName = file.getOriginalFilename();
            Path dfile = this.dirLocation.resolve(fileName);
            Files.copy(file.getInputStream(), dfile,StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e) {
            System.out.println("err");
            //throw new FileStorageException("Could not upload file");
        }
   return fileName;


    }

    @Override
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


}
}
