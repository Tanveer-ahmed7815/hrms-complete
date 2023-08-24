package com.te.flinko.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
	
	String uploadFile(MultipartFile file);
	
	Map<String, String> multipleFileUpload(MultipartFile[] files);

}
