package com.gw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gw.jpa.GWUser;
import com.gw.tools.UserTool;
import com.gw.utils.BaseTool;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GeneralTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testrestTemplate;
	

	Logger logger  = Logger.getLogger(this.getClass());

	@Autowired
	UserTool ut;

	@Autowired
	BaseTool bt;

	@Test
	void contextLoads() {
		
		
	}

	@Test
	@DisplayName("Testing adding/editing/removing user...")
	void testUser(){
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	HttpEntity request = new HttpEntity<>("type=host", headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/list", 
			request, 
			String.class);
		logger.debug("the result is: " + result);
		// assertThat(controller).isNotNull();
		assertThat(result).contains("[");

	}

	@Test
	String testResourceFiles(){

		Path resourceDirectory = Paths.get("src","test","resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		logger.debug(absolutePath);
		assertTrue(absolutePath.contains("resources"));
		return absolutePath;
	}


	@Test
   	@DisplayName("Subscription message service test ")
   	void testSubscriptionMessage() {
		
      	GWUser u = ut.getUserById("111111");

      	assertEquals(u.getUsername(), "publicuser");
   	}

	@Test
	@DisplayName("Testing if the front page is accessible..")
	void testFrontPage(){
		String result = this.testrestTemplate.getForObject("http://localhost:" + this.port + "/Geoweaver/web/geoweaver", String.class);
		// logger.debug("the result is: " + result);
		// assertThat(controller).isNotNull();
		assertThat(result).contains("Geoweaver");
		
	}

	@Test
	@DisplayName("Testing Dashboard...")
	void testDashboard(){
		// ResponseEntity<String> result = testrestTemplate.getForEntity("http://localhost:" + this.port + "/Geoweaver/web/dashboard", String.class);
		ResponseEntity result = this.testrestTemplate.postForEntity("http://localhost:" + this.port + "/Geoweaver/web/dashboard",
			"",
			String.class);
		// logger.debug("the dashboard result is: " + result);
		// assertThat(controller).isNotNull();
		assertEquals(200, result.getStatusCode().value());
		assertThat(result.getBody().toString()).contains("process_num");
	}

	@Test
	@DisplayName("Testing list of host, process, and workflow...")
	void testList(){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	HttpEntity request = new HttpEntity<>("type=host", headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/list", 
			request, 
			String.class);
		logger.debug("the result is: " + result);
		assertThat(result).contains("[");

		request = new HttpEntity<>("type=process", headers);
		result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/list", 
			request, 
			String.class);
		assertThat(result).contains("[");

		request = new HttpEntity<>("type=workflow", headers);
		result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/list", 
			request, 
			String.class);
		assertThat(result).contains("[");
	}

	@Test
	void testJSONEscape(){

		String jsonstr = "import os\nimport time";

		if(jsonstr.contains("\nimport")){

			logger.debug("import is detected");

		}else{

			logger.debug("import is not detected");
		}

		
		String jsonstr2 = "{\"cells\":[{\"cell_type\":\"markdown\"";

		if(jsonstr2.contains("\"cells\"")){

			logger.debug("cell is detected");

		}else{

			logger.debug("cell is not detected");
		}

	}


	// Geoweaver/web/search
	@Test
	@DisplayName("Testing search of host, process, and workflow...")
	void testSearchGlobal(){

		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	
		// Search for all available hosts
		logger.debug("\n\n##############\nTesting search of all hosts\n##############\n");
		HttpEntity request = new HttpEntity<>("type=host", headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			request, 
			String.class);
		// assertThat(controller).isNotNull();
		assertThat(result).contains("[");
		// logger.debug("Result contains specific host: " + result.contains("New Host GoogleE"));

		// Search for all available processes
		logger.debug("\n\n##############\nTesting search of all processes\n##############\n");
		request = new HttpEntity<>("type=process", headers);
		result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			request, 
			String.class);
		assertThat(result).contains("[");


		// Search for all available workflows
		logger.debug("\n\n##############\nTesting search of all workflows\n##############\n");
		request = new HttpEntity<>("type=workflow", headers);
		result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			request, 
			String.class);
		assertThat(result).contains("[");
	}


	@Test
	@DisplayName("Testing search of specific host.")
	void testSearchHost() throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Add new host
		String bultinjson = bt.readStringFromFile(bt.testResourceFiles()+ "/add_ssh_host.txt" );
    	HttpEntity request = new HttpEntity<>(bultinjson, headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/add", 
			request, 
			String.class);
		// assertThat(controller).isNotNull();
		assertThat(result).contains("id");
    	
		// Search for recently created host
		logger.debug("\n\n##############\nTesting search of specific host\n##############\n");
		HttpEntity searchRequest = new HttpEntity<>("type=host", headers);
		String searchResult = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			searchRequest, 
			String.class);
		assertThat(searchResult).contains("New Host");


		//Remove the added host
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = mapper.readValue(result, Map.class);
		String hid = String.valueOf(map.get("id"));


		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	HttpEntity DeleteRequest = new HttpEntity<>("id="+hid+"&type=host", headers);
		String DeleteResult = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/del", 
			DeleteRequest, 
			String.class);
		assertThat(DeleteResult).contains("done");

	}

	@Test
	@DisplayName("Testing search of specific python process.")
	void testSearchPythonProcess() throws Exception {

		// Add Python Process
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String pythonjson = bt.readStringFromFile(bt.testResourceFiles()+ "/add_python_process.json" );
    	HttpEntity request = new HttpEntity<>(pythonjson, headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/add/process", 
			request, 
			String.class);
		assertThat(result).contains("id");


		// Search for specific python processes
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		logger.debug("\n\n##############\nTesting search of specific python process\n##############\n");
		HttpEntity SearchRequest = new HttpEntity<>("type=process", headers);
		String SearchResult = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			SearchRequest, 
			String.class);
		logger.debug("Result contains specific python process: " + SearchResult.contains("testpython2"));



		// Delete added python process
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = mapper.readValue(result, Map.class);
		String pid = String.valueOf(map.get("id"));


    	HttpEntity DeleteRequest = new HttpEntity<>("id="+pid+"&type=process", headers);
		String DeleteResult = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/del", 
			DeleteRequest, 
			String.class);
		assertThat(DeleteResult).contains("done");
		
	}

	@Test
	@DisplayName("Testing search of specific workflow.")
	void testSearchWorkflow() throws Exception{

		// Add workflow
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String workflowjson = bt.readStringFromFile(this.testResourceFiles()+ "/add_workflow.json" );
    	HttpEntity request = new HttpEntity<>(workflowjson, headers);
		String result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/add/workflow", 
			request, 
			String.class);
		assertThat(result).contains("id");

		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = mapper.readValue(result, Map.class);

		String pid = String.valueOf(map.get("id"));
		assertNotNull(pid);


		// Search for specific workflow
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		logger.debug("\n\n##############\nTesting search of specific workflow\n##############\n");
		request = new HttpEntity<>("type=workflow", headers);
		result = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/search", 
			request, 
			String.class);
		logger.debug("Result contains specific workflow: " + result.contains("t2"));


		// Delete added workflow
    	HttpEntity DeleteRequest = new HttpEntity<>("id="+map.get("id")+"&type=workflow", headers);
		String DeleteResult = this.testrestTemplate.postForObject("http://localhost:" + this.port + "/Geoweaver/web/del", 
			DeleteRequest, 
			String.class);
		assertThat(DeleteResult).contains("done");
		
	}


}
