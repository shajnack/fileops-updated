package com.fileops.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.Data;
import lombok.Value;
import org.springframework.context.annotation.Bean;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@ConfigurationProperties(prefix = "dropbox.accessToken")
@Data
@Configuration
public class DropboxConfiguration {


    private String accessToken="sl.BKfQcMOk-bBEjNaLNc9K0aKylcY0qDKTYb3u2ed3Kh82SwwwfpAtQchFMnELsQHYVomCRX-HhvgoAQkwTXzJDChfz2uAlbLeh2aIFltucgrICtnpRt9tNZcswFWvIjGTrT0QoAs";

    @Bean
    public DbxClientV2 dropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("fileupdatedemo").build();
        return new DbxClientV2(config, accessToken);
    }

}