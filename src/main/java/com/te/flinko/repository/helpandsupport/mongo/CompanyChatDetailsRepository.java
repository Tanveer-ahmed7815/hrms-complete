package com.te.flinko.repository.helpandsupport.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.helpandsupport.mongo.CompanyAccountTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyChatDetails;

public interface CompanyChatDetailsRepository extends MongoRepository<CompanyChatDetails, String> {

	Optional<CompanyChatDetails> findByCompanyIdAndEmployeeOneAndEmployeeTwoAndConversationsDateLessThanEqual(
			Long companyId, String employeeOne, String employeeTwo, LocalDateTime date);

	Optional<CompanyChatDetails> findByCompanyIdAndEmployeeOneAndEmployeeTwo(Long companyId, String employeeOne,
			String employeeTwo);

	Optional<CompanyChatDetails> findByCompanyIdAndEmployeeOneInAndEmployeeTwoIn(Long companyId,
			List<String> employeeOneTwo, List<String> employeeTwoOne);
	
}
