package com.searchupload.aws.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	public boolean uploadFile(MultipartFile multipartFile);
	
}
