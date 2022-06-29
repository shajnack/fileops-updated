package com.fileops.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.fileops.exception.DropboxException;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DropboxServiceImpl implements DropboxService {

    private final DbxClientV2 client;

    public DropboxServiceImpl(DbxClientV2 client) {
        this.client = client;
    }

    @Override
    public InputStream downloadFile(String filePath) throws DropboxException, DbxException {
        return  client.files().download(filePath).getInputStream();
    }

}
