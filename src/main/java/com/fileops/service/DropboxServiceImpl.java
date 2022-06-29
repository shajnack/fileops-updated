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

  //  @Override
  //  public FileMetadata uploadFile(String filePath, InputStream fileStream) throws DropboxException {
   //     return handleDropboxAction(() -> client.files().uploadBuilder(filePath).uploadAndFinish(fileStream),
    //            String.format("Error uploading file: %s", filePath));
   // }



   /* private <T> T getMetadata(String path, Class<T> type, String message) {
        Metadata metadata = handleDropboxAction(() -> client.files().getMetadata(path),
                String.format("Error accessing details of: %s", path));

        checkIfMetadataIsInstanceOfGivenType(metadata, type, message);
        return (T) metadata;
    }

    private <T> void checkIfMetadataIsInstanceOfGivenType(Metadata metadata, Class<T> validType, String exceptionMessage) {
        boolean isValidType = validType.isInstance(metadata);
        if (!isValidType) {
            throw new DropboxException(exceptionMessage);
        }
    }*/
}
