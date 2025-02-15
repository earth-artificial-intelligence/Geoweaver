package com.gw;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gw.database.UserRepository;
import com.gw.jpa.GWUser;
import com.gw.tools.UserTool;
import com.gw.utils.BaseTool;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileTest {
    

    @LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testrestTemplate;

	Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	UserTool ut;

	@Autowired
	BaseTool bt;

    @Test
    void testTempFileURL(){
		
        String testfilecontent = bt.readStringFromFile(bt.testResourceFiles() + "/test_file.txt");

        bt.writeString2File(testfilecontent,  bt.getFileTransferFolder() + "/random_mars_rock.txt");

        String result = this.testrestTemplate.getForObject("http://localhost:" + this.port + "/Geoweaver/web/file/random_mars_rock.txt", 
                String.class);
		
		assertThat(result).contains("testing_beautiful_mars_string");

    }

	@Test
    void testFileDownloadController(){

        String testfilecontent = bt.readStringFromFile(bt.testResourceFiles() + "/test_file.txt");

        bt.writeString2File(testfilecontent,  bt.getFileTransferFolder() + "/random_mars_rock.txt");

        String result = this.testrestTemplate.getForObject("http://localhost:" + this.port + "/Geoweaver/download/temp/random_mars_rock.txt", 
                String.class);
		
		assertThat(result).contains("testing_beautiful_mars_string");

    }




	@Test
	void testSSHFileUploading(){

		String testfileuploadingjson = bt.readStringFromFile(bt.testResourceFiles() + "/ssh_upload.json");
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity request = new HttpEntity<>(testfileuploadingjson, headers);

		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/upload",
				request,
				String.class);

		assertThat(result).contains("Internal");

	}

	@Test
	void testSSHFileRetrieve(){

		String testfileretrievejson = bt.readStringFromFile(bt.testResourceFiles() + "/ssh_retrieve.json");
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity request = new HttpEntity<>(testfileretrievejson, headers);

		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/retrieve",
				request,
				String.class);

		assertThat(result).contains("Internal");

	}



}
