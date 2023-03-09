package com.searchupload.aws.serviceimpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchupload.aws.model.Fileupload;

public class ElasticServiceImplTest {

	public Fileupload fileuploadc;
	private RestHighLevelClient client;
    private ObjectMapper objectMapper;
    private Map<String,Object> sourcemap;
    
    String host = "search-awses-xbcb6l2yduvilk4celoefjbn6e.ap-south-1.es.amazonaws.com";
    String index = "index";
    String type = "_doc";
    String name = "";
	
	@Before
	public void testElasticServiceImpl() throws IOException {
		File file = createTempFile();
		Fileupload fileupload = new Fileupload(file);
		name = file.getName();
        this.fileuploadc = fileupload;
        this.objectMapper = new ObjectMapper();
        UUID uuid = UUID.randomUUID();
		fileuploadc.setId(uuid.toString());
		sourcemap= convertfileDocumentToMap(fileuploadc, this.objectMapper);
		client = new RestHighLevelClient(RestClient.builder(new HttpHost(host))); 
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateImageDocument() throws IOException {
		System.out.println(fileuploadc);
		IndexRequest indexRequest = new IndexRequest(index, type, fileuploadc.getId())
                .source(sourcemap, XContentType.JSON);
		Assert.assertNotNull(indexRequest);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);       
        Assert.assertNotNull(indexResponse);
        
        DeleteRequest request = new DeleteRequest("index",fileuploadc.getId());
        request.id(fileuploadc.getId());
        DeleteResponse deleteResponse = client.delete(request,RequestOptions.DEFAULT);
        System.out.println("Deleted");
        Assert.assertNotNull(deleteResponse);
	}

	@Test
	public void testFindfileByName() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("name",name)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        
        Assert.assertNotNull(searchResponse);
        
        DeleteRequest request = new DeleteRequest("index",fileuploadc.getId());
        DeleteResponse deleteResponse = client.delete(request,RequestOptions.DEFAULT);
        System.out.println("Deleted");
        Assert.assertNotNull(deleteResponse);
	}
	
	private Map<String, Object> convertfileDocumentToMap(Fileupload fileupload, ObjectMapper objectMapper) {
		if(fileupload!=null && fileupload.getSize() > 0) {
			System.out.println("Inside if");
			if (objectMapper.convertValue(fileupload, Map.class)!=null)
				Assert.assertNotNull(objectMapper.convertValue(fileupload, Map.class));
			return objectMapper.convertValue(fileupload, Map.class);
		}
		else {
        return objectMapper.convertValue(fileupload, Map.class);
		}
		
    }
	
	private File createTempFile() throws IOException {
		BufferedOutputStream out = null;
		try {
			File file = File.createTempFile("s3test" + UUID.randomUUID(), ".txt");

			byte[] zeroes = new byte[1024];
			System.out.println(zeroes);
			out = new BufferedOutputStream(new FileOutputStream(file));
			for (int i=0; i < 100; i++) {
				out.write(zeroes);
			}
			System.out.println(file);
			return file;
		} finally {
			if (out != null) out.close();
		}
	}
	
	@Test
	public void test_http_response_code_200() throws ClientProtocolException, IOException {
		
		HttpUriRequest request = new HttpGet( "http://localhost:8080/api/file/all");
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		int code = response.getStatusLine().getStatusCode();
		
		Assert.assertEquals(200, code);
	}
	
	@Test
	public void test_http_response_code_400() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet( "http://localhost:8080/api/file/search");
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		int code = response.getStatusLine().getStatusCode();
		
		Assert.assertEquals(400, code);	
	}
	
	@Test
	public void test_http_response_code_500() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet("http://localhost:8080/api/file/search?name=abc.txt");
		request.addHeader(host,host);
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		int code = response.getStatusLine().getStatusCode();
		
//		HttpUriRequest drequest = new HttpDelete("https://search-awses-xbcb6l2yduvilk4celoefjbn6e.ap-south-1.es.amazonaws.com/indexs");
//		HttpResponse response = HttpClientBuilder.create().build().execute(drequest);
//		int code = response.getStatusLine().getStatusCode();
		
//		Assert.assertEquals(500, code);
	}
	

}
