package com.searchupload.aws.service;

import java.io.IOException;
import java.util.List;

import com.searchupload.aws.model.Fileupload;

public interface ElasticService {
	
	public String CreateImageDocument(Fileupload fileupload) throws IOException;
	
	public List<Fileupload> findfileByName(String name) throws Exception;

	public List<Fileupload> findAll() throws Exception;
}
