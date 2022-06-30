package com.fileops;

import com.fileops.config.DropboxConfiguration;
import com.fileops.controller.FileOpsRestController;
import com.fileops.service.DropboxService;
import com.fileops.service.DropboxServiceImpl;
import com.fileops.service.FileOpsService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestFileOpsRestController {

    private MockMvc mockMvc;

    @Mock
    private FileOpsService fileOpsService;

    @Mock
    private DropboxServiceImpl dropboxServiceImpl;
    @InjectMocks
    private FileOpsRestController fileOpsRestController;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(fileOpsRestController)
                .build();
    }

    //POST
    @Test
    public void createGitTest() throws Exception {

        mockMvc.perform(post("/api/createGitRepo/mockrepoNew9"))
                .andExpect(status().isOk());
        //.andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }
    @Test
    public void uploadFileToGit() throws Exception {

        Path dfile = Paths.get("/Users/shajnack/FileOpsTest/new9.txt");
        Mockito.when(fileOpsService.uploadFilePath(Mockito.any())).thenReturn(dfile);

        ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "new9.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadFileToGit/")
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());

    }


    @Test
    public void downloadFileFromGit() throws Exception {
        mockMvc.perform(get("/api/downloadFileFromGit/README.md"))
                .andExpect(status().isOk());
    }

    @Test
    public void downloadFileFromDropbox() throws Exception {

        InputStream mockInputstream = new FileInputStream("/Users/shajnack/FileOpsTest/hi5.txt");
        Mockito.when(dropboxServiceImpl.downloadFile(Mockito.anyString())).thenReturn(mockInputstream);

        mockMvc.perform(get("/api/downloadFileFromDropbox/")
                .param("filepath", "/F1/new.txt"))
                .andExpect(status().isOk());

    }

    @Test
    public void uploadFileToLocalDiskTest() throws Exception {

        Path dfile = Paths.get("/Users/shajnack/FileOpsTest/");
        Mockito.when(fileOpsService.uploadFilePath(Mockito.any())).thenReturn(dfile);

        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadFileToLocalDisk/")
                        .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

}