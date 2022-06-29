package com.fileops.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.Data;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class DropboxConfiguration {
    /**Need to change it to request param*/
    private String accessToken="sl.BKfQLDPXphiESLFl59WPj8czDLGpUxFG8rYlwkdQpDYkmN72jCHicP7PpfhuGD0K0KM-_0-LGABhak-RuMMiYhmrZ6YpQLVK5iwGwTOxO8KWvfKHoB0dwda0Xl2hodwEvJ6sKsk";

    @Bean
    public DbxClientV2 dropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("fileupdatedemo").build();
        return new DbxClientV2(config, accessToken);
    }

}