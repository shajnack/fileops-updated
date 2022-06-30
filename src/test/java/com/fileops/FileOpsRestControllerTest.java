package com.fileops;

import com.fileops.controller.FileOpsRestController;
import com.fileops.service.DropboxServiceImpl;
import com.fileops.service.FileOpsServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileOpsRestControllerTest {

    private MockMvc mockMvc;
    @Mock
    private FileOpsServiceImpl fileOpsServiceimpl;
    @Mock
    private DropboxServiceImpl dropboxServiceImpl;
    @InjectMocks
    private FileOpsRestController fileOpsRestController;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(fileOpsRestController)
                .build();
    }
    //@TempDir
    //static Path tempDir;
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    //POST
    @Test
    public void createGitTest() throws Exception {

        mockMvc.perform(post("/api/createGitRepo/mockrepoNew12")
                .param("gitAuthToken",""))
                .andExpect(status().isOk());
        //.andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }



    @Test
    public void uploadFileToGitTest() throws Exception {

        File tempFile = testFolder.newFile("file1.txt");
        File tempFolder = testFolder.newFolder("folder");
        System.out.println("Test folder: " + testFolder.getRoot());

       /* Option2 with @TempDir
       String testFileName = "testFile.txt";
        String testFileContent = "test text";

        Path testFile = tempDir.resolve(testFileName);
        Files.createFile(testFile);
        Files.write(testFile, testFileContent.getBytes());
        when(fileOpsService.uploadFilePath(Mockito.any())).thenReturn(testFile);*/

        //Option1 with local dir
        /** Path dfile = Paths.get("/Users/shajnack/FileOpsTest/new11.txt");
         Mockito.when(fileOpsService.uploadFilePath(Mockito.any())).thenReturn(tempFile.toPath());*/

        Mockito.when(fileOpsServiceimpl.uploadFilePath(Mockito.any())).thenReturn(tempFile.toPath());

        ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", tempFile.getName(), MediaType.TEXT_PLAIN_VALUE, "Heloo".getBytes());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadFileToGit/")
                        //.file(mockMultipartFile)
                        .file(mockMultipartFile)
                        .param("gitAuthToken", "")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());

    }


    @Test
    public void downloadFileFromGitTest() throws Exception {

        File tempFile = testFolder.newFile("file.txt");
        File tempFolder = testFolder.newFolder("folder");
        System.out.println("Test folder: " + testFolder.getRoot());
        Mockito.when(fileOpsServiceimpl.getDownloadDirLocation()).thenReturn(tempFolder.toPath());

        mockMvc.perform(get("/api/downloadFileFromGit/file.txt")
                .param("gitAuthToken",""))
                .andExpect(status().isOk());
    }

    @Test
    public void downloadFileFromDropboxTest() throws Exception {

        File tempFile = testFolder.newFile("new.txt");
        File tempFolder = testFolder.newFolder("folder");
        System.out.println("Test folder: " + testFolder.getRoot());
        Mockito.when(fileOpsServiceimpl.getDownloadDirLocation()).thenReturn(tempFolder.toPath());

        InputStream anyInputStream = new ByteArrayInputStream("Test data from dropbox".getBytes());
        Mockito.when(dropboxServiceImpl.downloadFile(Mockito.anyString())).thenReturn(anyInputStream);

        /** Option
         InputStream mockInputstream = new FileInputStream("/Users/shajnack/FileOpsTest/hi5.txt");
         Mockito.when(dropboxServiceImpl.downloadFile(Mockito.anyString())).thenReturn(mockInputstream);*/

        mockMvc.perform(get("/api/downloadFileFromDropbox/")/**testing api calls*/
                .param("filepath", tempFolder.toString()))
                .andExpect(status().isOk());

    }

    @Test
    public void uploadFileToLocalDiskTest() throws Exception {

        /**Option
         * Path dfile = Paths.get("/Users/shajnack/FileOpsTest/");
         Mockito.when(fileOpsService.uploadFilePath(Mockito.any())).thenReturn(dfile);**/

        File tempFile = testFolder.newFile("file1.txt");
        File tempFolder = testFolder.newFolder("folder");
        System.out.println("Test folder: " + testFolder.getRoot());
        Mockito.when(fileOpsServiceimpl.uploadFilePath(Mockito.any())).thenReturn(tempFile.toPath());

        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadFileToLocalDisk/")
                        .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

}