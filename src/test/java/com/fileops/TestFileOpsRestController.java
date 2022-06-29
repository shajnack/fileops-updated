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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestFileOpsRestController {

    private MockMvc mockMvc;
    /**
     * provided by spring framework to test servlet calls
     */

    @Mock
    private FileOpsService fileOpsService;

    @Mock
    private DropboxServiceImpl dropboxServiceImpl;



    @InjectMocks
    private FileOpsRestController fileOpsRestController;

    /**
     * mocking resource
     */

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(fileOpsRestController)
                .build();
    }

    //POST
    @Test
    public void createGitTest() throws Exception {
        //  mockMvc.perform( MockMvcRequestBuilders.post("/api/createGitRepo/mockrepo"))
        mockMvc.perform(post("/api/createGitRepo/mockrepoNew1"))
                .andExpect(status().isOk());
        //.andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").exists());
    }

    @Test
    public void uploadFileToLocalDiskTest() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();


/**worked
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        mockMvc.perform(fileUpload("/api/uploadFileToLocalDisk/").file(file))
                .andExpect(status().isOk());
*/

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
     MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadFileToLocalDisk/")

                        .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void uploadFileToGit() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "hellosir.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
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


        InputStream mockInputstream = new FileInputStream("/Users/aftabkarim/Desktop/hi.txt");

        Mockito.when(dropboxServiceImpl.downloadFile(Mockito.anyString())).thenReturn(mockInputstream);

        mockMvc.perform(get("/api/downloadFileFromDropbox/")
                .param("filepath", "/F1/new.txt"))
                .andExpect(status().isOk());

    }
   /** @Autowired
    private DropboxService dropboxService;
    private static final String TEST_FOLDER_PATH = "/Test Folder";
    private static final String TEST_FILE_PATH = String.format("%s/%s", TEST_FOLDER_PATH, "testFile.txt");
    private static final Integer TEST_FILE_SIZE = 17;

    @Test
    public void downloadFile_shouldReturnNotEmptyInputStream() throws Exception {
        // when
        InputStream inputStream = dropboxService.downloadFile(TEST_FILE_PATH);

        // then
        assertThat(inputStream.available()).isEqualTo(TEST_FILE_SIZE);
    }

    @Test
    public void downloadFile_shouldThrowExceptionIfFileNotExists() {
        exceptions.expect(DropboxException.class);
        dropboxService.downloadFile("not-existing-file");
    }*/
}

    /**@Test
    public void shouldUploadFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("testupload.txt", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        ResponseEntity<String> response = this.restTemplate.postForEntity("/", map,
                String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().toString())
                .startsWith("http://localhost:" + this.port + "/");
        then(storageService).should().store(any(MultipartFile.class));
    }

    @Test
    public void shouldDownloadFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("testupload.txt", getClass());
        given(this.storageService.loadAsResource("testupload.txt")).willReturn(resource);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity("/files/{filename}", String.class, "testupload.txt");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"testupload.txt\"");
        assertThat(response.getBody()).isEqualTo("Spring Framework");
    }


}*/
/**
 public static String asJsonString(final Object obj) {
 try {
 return new ObjectMapper().writeValueAsString(obj);
 } catch (Exception e) {
 throw new RuntimeException(e);
 }
 }

 //PUT
 @Test
 public void updateEmployeeAPI() throws Exception
 {
 mvc.perform( MockMvcRequestBuilders
 .put("/employees/{id}", 2)
 .content(asJsonString(new EmployeeVO(2, "firstName2", "lastName2", "email2@mail.com")))
 .contentType(MediaType.APPLICATION_JSON)
 .accept(MediaType.APPLICATION_JSON))
 .andExpect(status().isOk())
 .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstName2"))
 .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastName2"))
 .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email2@mail.com"));
 }*/
