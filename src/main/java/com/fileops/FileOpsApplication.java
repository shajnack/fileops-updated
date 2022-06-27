package com.fileops;


import com.fileops.config.FileUploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileUploadProperties.class
})
public class FileOpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileOpsApplication.class, args);


	}

}




