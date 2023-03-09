package com.searchupload.aws.serviceimpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;

public class ImageServiceImplTest {
	
	private static String bucketName;
	private static String objectKey;
	static File filed;
	public static final AWSCredentials cred =
			new ClasspathPropertiesFileCredentialsProvider("AwsCredentials.properties").getCredentials();

	@BeforeClass
	public static void createTestBucket() throws Exception {
		bucketName = "s3testbucket" + UUID.randomUUID();
		objectKey = "s3TestObject" + UUID.randomUUID();
		AmazonS3 client = new AmazonS3Client(cred);
		client.createBucket(bucketName);
		InputStream in = new StringInputStream("Hello!");
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength("Hello!".length());
		client.putObject(bucketName, objectKey, in, metadata);
		in.close();
	}
	
	@Test
	public void checkUploadObject() throws Exception {
		AmazonS3 client = new AmazonS3Client(cred);
		File file = createTempFile();
		filed = file;
		PutObjectResult result = client.putObject(bucketName, file.getName(), file);
		client.deleteObject(bucketName, file.getName());
		Assert.assertNotNull(result);
	}
		
	
	@Test
	 public void deleteNonExistingObject() {
		AmazonS3 s3Client = new AmazonS3Client(cred);
	     s3Client.deleteObject(bucketName, UUID.randomUUID().toString());
	 }
	
	@Test
	public void deleteExistingObject() throws IOException {
		AmazonS3 s3Client = new AmazonS3Client(cred);
		File file = createTempFile();
		PutObjectResult result = s3Client.putObject(bucketName, file.getName(), file);
		s3Client.deleteObject(bucketName, file.getName());
	}
	
	private File createTempFile() throws IOException {
		BufferedOutputStream out = null;
		try {
			File file = File.createTempFile("s3test" + UUID.randomUUID(), ".txt");

			byte[] zeroes = new byte[1024];
			out = new BufferedOutputStream(new FileOutputStream(file));
			for (int i=0; i < 100; i++) {
				out.write(zeroes);
			}
			return file;
		} finally {
			if (out != null) out.close();
		}
	}
	
	

	public void 
	givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
	  throws ClientProtocolException, IOException {
	 
	   // Given
	   String jsonMimeType = "application/json";
	   HttpUriRequest request = new HttpGet( "http://localhost:8080/api/file/all" );

	   // When
	   HttpResponse response = HttpClientBuilder.create().build().execute( request );

	   // Then
	   String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
	   Assert.assertEquals( jsonMimeType, mimeType );
	}
	
	@AfterClass
	public static void deleteTestBucket() {
		AmazonS3 client = new AmazonS3Client(cred);
		ObjectListing objectListing = client.listObjects(bucketName);
        Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
        while (objIter.hasNext()) {
            client.deleteObject(bucketName, objIter.next().getKey());
        }
        client.deleteBucket(bucketName);
	}
	

}
