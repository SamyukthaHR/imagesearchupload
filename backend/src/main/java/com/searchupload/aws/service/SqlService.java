package com.searchupload.aws.service;

import com.searchupload.aws.model.Fileupload;

public interface SqlService {
	public boolean uploadtodb(Fileupload fileupload);
}
