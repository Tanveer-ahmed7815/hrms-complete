package com.te.flinko.repository.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.Department;

public interface DepartmentInfoRepository extends JpaRepository<Department, Long> {
	Optional<Department> findByDepartmentName(String departmentName);

	Optional<List<Department>> findByDepartmentNameIgnoreCaseIn(List<String> departmentNames);
}
