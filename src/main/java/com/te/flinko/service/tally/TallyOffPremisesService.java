package com.te.flinko.service.tally;

import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.dto.tally.TallyDetailsDTO;

public interface TallyOffPremisesService {

	public TallyDetailsDTO tallyDetails(MultipartFile master, MultipartFile transaction, String flag, Long companyId);
}
