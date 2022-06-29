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


    private String accessToken="sl.BKYYk52fBoASFWnp5Mju0LpvsSlOoKUMag6DmlZxeDaqPlnK1NF80c8TTc1mH_AoVmlN0NvvFooK55IiVpk9BrHqHPk0fUXfEPQ87qR6yqErid1feNPu5mu_CAaXjX6kcHBEZGiix2oc";

    @Bean
    public DbxClientV2 dropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("fileupdatedemo").build();
        return new DbxClientV2(config, accessToken);
    }

}