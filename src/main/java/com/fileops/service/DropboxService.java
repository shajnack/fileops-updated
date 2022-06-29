package com.fileops.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import java.io.InputStream;

public interface DropboxService {

    InputStream downloadFile(String filePath) throws DbxException;

   // FileMetadata uploadFile(String filePath, InputStream fileStream);

}