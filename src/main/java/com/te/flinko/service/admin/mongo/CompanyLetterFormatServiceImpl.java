package com.te.flinko.service.admin.mongo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.dto.admin.CompanyLetterDTO;
import com.te.flinko.entity.admin.mongo.CompanyLetterFormat;
import com.te.flinko.exception.FailedToUploadException;
import com.te.flinko.repository.admin.CompanyLetterFormatRepository;
import com.te.flinko.util.FileToMultipartConverter;
import com.te.flinko.util.Generator;
import com.te.flinko.util.S3UploadFile;

@Service
public class CompanyLetterFormatServiceImpl implements CompanyLetterFormatService {

	@Autowired
	private CompanyLetterFormatRepository companyLetterFormatRepository;
	@Autowired
	private FileToMultipartConverter fileToMultipartConverter;

	@Autowired
	private S3UploadFile s3UploadFile;

	@Autowired
	private Generator generator;

	@Transactional
	@Override
	public Boolean addLetterFormat(CompanyLetterDTO companyLetterDTO, Long companyId, MultipartFile file) {
		if (file.isEmpty()) {
			throw new FailedToUploadException("File Does Not Exists");
		}
		String uploadFile = s3UploadFile.uploadFile(file);

		List<CompanyLetterFormat> companyLetterFormatList = companyLetterFormatRepository.findByCompanyId(companyId);

		if (companyLetterFormatList.isEmpty()) {
			Map<String, String> letters = new LinkedHashMap<>();
			letters.put(companyLetterDTO.getType(), uploadFile);
			CompanyLetterFormat companyLetterFormat = CompanyLetterFormat.builder().companyId(companyId)
					.letterId(generator.generateSequence("company_letter_format_sequence")).letters(letters).build();
			companyLetterFormatRepository.save(companyLetterFormat);
		} else {
			CompanyLetterFormat companyLetterFormat = companyLetterFormatList.get(companyLetterFormatList.size() - 1);
			Map<String, String> letters = companyLetterFormat.getLetters() == null ? new LinkedHashMap<>()
					: companyLetterFormat.getLetters();
			letters.put(companyLetterDTO.getType(), uploadFile);
			companyLetterFormat.setLetters(letters);
			companyLetterFormatRepository.save(companyLetterFormat);
		}
		return true;

	}

	@Override
	public List<CompanyLetterDTO> getAllLetterDetails(Long companyId) {
		List<CompanyLetterDTO> companyLetterDTOList = new ArrayList<>();
		List<CompanyLetterFormat> companyLetterFormatList = companyLetterFormatRepository.findByCompanyId(companyId);
		if (!companyLetterFormatList.isEmpty()) {
			Map<String, String> letters = companyLetterFormatList.get(companyLetterFormatList.size() - 1).getLetters();
			if (letters != null) {
				for (Entry<String, String> letter : letters.entrySet()) {
					companyLetterDTOList
							.add(CompanyLetterDTO.builder().type(letter.getKey()).url(letter.getValue()).build());
				}
			}
		}
		return companyLetterDTOList;
	}

	@Override
	public CompanyLetterDTO getLetterDetails(Long companyId, CompanyLetterDTO companyLetterDTO) {
		List<CompanyLetterFormat> companyLetterFormatList = companyLetterFormatRepository.findByCompanyId(companyId);
		if (!companyLetterFormatList.isEmpty()) {
			Map<String, String> letters = companyLetterFormatList.get(companyLetterFormatList.size() - 1).getLetters();
			if (letters != null) {
				String url = letters.get(companyLetterDTO.getType());
				if (url != null) {
					File fileUrl = new File(url);
					MultipartFile convert;
					byte[] bytes=null;
					try {
						convert = fileToMultipartConverter.convert(fileUrl);
						 bytes = convert.getBytes();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					return CompanyLetterDTO.builder().type(companyLetterDTO.getType()).fileUrl(bytes).url(url)
							.build();
				}
			}
		}
		return null;
	}

}
