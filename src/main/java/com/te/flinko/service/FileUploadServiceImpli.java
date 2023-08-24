package com.te.flinko.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.exception.FailedToUploadException;
import com.te.flinko.util.S3UploadFile;

@Service
public class FileUploadServiceImpli implements FileUploadService {

	@Autowired
	private S3UploadFile s3UploadFile;

	@Override
	public String uploadFile(MultipartFile file) {
		if (!file.isEmpty()) {
			return s3UploadFile.uploadFile(file);
		}
		throw new FailedToUploadException("File Does Not Exists");
	}

	@Override
	public Map<String, String> multipleFileUpload(MultipartFile[] files) {
		Map<String, String> uploadedURLDetails = new LinkedHashMap<>();
		for (MultipartFile multipartFile : files) {
			if (!multipartFile.isEmpty()) {
				String uploadFile = s3UploadFile.uploadFile(multipartFile);
				uploadedURLDetails.put(uploadFile,multipartFile.getOriginalFilename());
			}
		}
		return uploadedURLDetails;
	}

}
