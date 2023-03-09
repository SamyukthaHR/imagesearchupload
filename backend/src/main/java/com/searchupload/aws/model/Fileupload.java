package com.searchupload.aws.model;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class Fileupload {
	private String id;
	private String name;
	private String type;
	private long size;
	
	public Fileupload(MultipartFile file) {
		this.name = file.getOriginalFilename();
		String contenttype = file.getContentType();
		String[] typeext = contenttype.split("/");
		this.type = typeext[typeext.length - 1];
		this.size = file.getSize();	
	}
	
	public Fileupload(File file) {
		this.name = file.getName();
		this.type = "txt";
		this.size = file.length();
	}
	
	public Fileupload() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
