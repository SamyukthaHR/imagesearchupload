package com.searchupload.aws.controller;

import java.util.List;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchupload.aws.model.Fileupload;
import com.searchupload.aws.service.ElasticService;
import com.searchupload.aws.service.ImageService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class UploadController {
	private Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	ImageService imageService;
	
	@Autowired
	ElasticService elasticService;
	
	@PostMapping("/api/file/upload")   
	@CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<String> uploadMultipartFile(@RequestParam("file") MultipartFile file) {
    	logger.info("Entering controller");
    	String checking = file.getOriginalFilename();
    	logger.info(checking);
    	if(imageService.uploadFile(file)) {
    	return ResponseEntity.ok().body("File upload Success");
    	}
    	return ResponseEntity.status(500).body("Upload failed");
    }    
	
	
   	@GetMapping("/api/file/search")
    public ResponseEntity<List<Fileupload>> searchByName(@RequestParam(value = "name") String name) throws Exception {
        List<Fileupload> filedetails = elasticService.findfileByName(name);
        return ResponseEntity.ok().body(filedetails);
    }
   	
   	@GetMapping("/api/file/all")
   	public ResponseEntity<List<Fileupload>> getall() throws Exception {
   		List<Fileupload> allfiles = elasticService.findAll();
   		return ResponseEntity.ok().body(allfiles);
   	}
}
