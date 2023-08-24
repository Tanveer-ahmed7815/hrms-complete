package com.te.flinko.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileToMultipartConverter {

	public MultipartFile convert(File file) throws IOException {
		Path path = Paths.get(file.getAbsolutePath());
		String fileName = StringUtils.cleanPath(path.getFileName().toString());
		String contentType = Files.probeContentType(path);
		byte[] content = Files.readAllBytes(path);

		return new MultipartFile() {
			@Override
			public String getName() {
				return "file";
			}

			@Override
			public String getOriginalFilename() {
				return fileName;
			}

			@Override
			public String getContentType() {
				return contentType;
			}

			@Override
			public boolean isEmpty() {
				return content == null || content.length == 0;
			}

			@Override
			public long getSize() {
				return content != null ? content.length : 0;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return content;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(content);
			}

			@Override
			public void transferTo(File file) throws IOException, IllegalStateException {
				Files.write(file.toPath(), content);
			}
		};
	}
}
