package com.qa.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.base.TestBase;
import com.qa.client.RestClient;
import com.qa.data.Users;

public class PostAPITest extends TestBase{
	TestBase testBase;
	String serviceUrl;
	String apiUrl;
	String url;
	RestClient restClient;
	CloseableHttpResponse closeableHttpResponse;
	
	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException {
		testBase = new TestBase();
		serviceUrl = prop.getProperty("URL");
		apiUrl = prop.getProperty("serviceURL");
		
		url = serviceUrl + apiUrl;
		
	}
	
	@Test
	public void postAPITest() throws StreamWriteException, DatabindException, IOException {
		restClient = new RestClient();
		
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type","application/json");
		
		//Jackson 3API:
		ObjectMapper mapper = new ObjectMapper();
		Users users = new Users("Ram","leader");
		
		//object to json file:
		mapper.writeValue(new File("/Users/ramkumars/eclipse-workspace/restapi/src/main/java/com/qa/data/users.json"), users);
		
		//object to json in string:
		String usersJsonString = mapper.writeValueAsString(users);
		System.out.println(usersJsonString);
		
		closeableHttpResponse = restClient.post(url, usersJsonString, headerMap);
		
		//1. Status Code:
		int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(statusCode, testBase.RESPONSE_STATUS_CODE_201);
		
		//2. JsonString:
		String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
		JSONObject responseJson = new JSONObject(responseString);
		System.out.println("The Response from API is : " +responseJson);
		
		Users usersResObj = mapper.readValue(responseString, Users.class);
		System.out.println(usersResObj);
		
		Assert.assertTrue(users.getName().equals(usersResObj.getName()));
		Assert.assertTrue(users.getJob().equals(usersResObj.getJob()));
		
		System.out.println(usersResObj.getCreatedAt());
		System.out.println(usersResObj.getId());
	}

}
