package com.searchupload.aws.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.searchupload.aws.model.Fileupload;
import com.searchupload.aws.service.ElasticService;
import com.searchupload.aws.service.ImageService;
import com.searchupload.aws.service.SqlService;

@Service
public class ImageServiceImpl implements ImageService {

	private Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

	@Autowired
	private AmazonS3 s3client;

	@Autowired
	private SqlService sqlService;

	@Autowired
	private ElasticService elasticService;

	Fileupload fileupload;

	@Value("${jsa.s3.bucket}")
	private String bucketName;

	@Override
	public boolean uploadFile(MultipartFile multipartFile) {
		fileupload = new Fileupload(multipartFile);
		logger.info("Entered serviceimpl uploadfile");
		logger.info("Accessing each file");
		File file = convertMultiPartFileToFile(multipartFile);
		String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
		logger.info(uniqueFileName);
		if(!(uploadFileToS3bucket(uniqueFileName, file, bucketName))) {
			return false;
		};
		return true;
	}

	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			logger.error("Error converting multipartFile to file", e);
		}
		return convertedFile;
	}

	private boolean uploadFileToS3bucket(String fileName, File file, String bucketName) {
		logger.info("Enetered s3client method to upload");
		try {
			s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
			logger.info("Upload Successful!!");
			if(sqlService.uploadtodb(fileupload)) {
			String ret = elasticService.CreateImageDocument(fileupload);
			logger.info(ret);
			}
			else {
				s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
				logger.info("Upload failed");
				return false;
			}
		} catch (Exception ace) {
			logger.error("Caught Client exception: ", ace);
		}
		return true;
	}

}
