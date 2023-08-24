package com.te.flinko.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.ReportFile;

public interface ReportFileRepository extends MongoRepository<ReportFile, String> {
	Optional<ReportFile> findByReportId(Long reportId);
}
