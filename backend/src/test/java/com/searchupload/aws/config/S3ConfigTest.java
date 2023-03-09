package com.searchupload.aws.config;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3ConfigTest {
	private static String bucketName;
	private static String objectKey;
	
	public static final AWSCredentials cred =
			new ClasspathPropertiesFileCredentialsProvider("AwsCredentials.properties").getCredentials();

	@Test
	public void testS3client() {
		bucketName = "s3testbucket" + UUID.randomUUID();
		objectKey = "s3TestObject" + UUID.randomUUID();
		AmazonS3 client = new AmazonS3Client(cred);	
	}

}
