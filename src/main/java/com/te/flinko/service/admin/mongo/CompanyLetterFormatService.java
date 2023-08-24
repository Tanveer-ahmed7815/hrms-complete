package com.te.flinko.service.admin.mongo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.dto.admin.CompanyLetterDTO;

public interface CompanyLetterFormatService {

	Boolean addLetterFormat(CompanyLetterDTO companyLetterDTO, Long companyId, MultipartFile file);
	
	List<CompanyLetterDTO> getAllLetterDetails(Long companyId);
	
	CompanyLetterDTO getLetterDetails(Long companyId, CompanyLetterDTO companyLetterDTO);
}
