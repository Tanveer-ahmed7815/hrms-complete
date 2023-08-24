package com.te.flinko.repository.admin;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.admin.mongo.CompanyLetterFormat;

@Repository
public interface CompanyLetterFormatRepository extends MongoRepository<CompanyLetterFormat, String>{
	
	List<CompanyLetterFormat> findByCompanyId(Long companyId);

}
