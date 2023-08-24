package com.te.flinko.service.admin.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.hr.EmployeeBasicDetailsDTO;
import com.te.flinko.dto.hr.mongo.EmployeeLetterBasicDTO;
import com.te.flinko.dto.hr.mongo.EmployeeLetterDTO;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.EmployeeLetterDetails;
import com.te.flinko.entity.employee.mongo.LetterDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeLetterDetailsRepository;

@Service
public class AdminLetterApprovalServiceImpl implements AdminLetterApprovalService {

	@Autowired
	private EmployeeLetterDetailsRepository employeeLetterDetailsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Override
	public List<EmployeeLetterBasicDTO> getLetters(Long companyId, String status) {
		if (status.equalsIgnoreCase("Pending")) {
			return getPendingLetters(companyId);
		} else if (status.equalsIgnoreCase("Approved")) {
			return getApprovedLetters(companyId);
		} else {
			return getRejectedLetters(companyId);
		}
	}

	private List<EmployeeLetterBasicDTO> getPendingLetters(Long companyId) {
		List<EmployeeLetterBasicDTO> employeeLetterBasicDTOList = new ArrayList<>();
		List<EmployeeLetterDetails> letterDetails = employeeLetterDetailsRepository
				.findByCompanyIdAndLettersIsApproved(companyId, null);
		if (!letterDetails.isEmpty()) {
			List<EmployeeBasicDetailsDTO> employeeList = employeePersonalInfoRepository.getEmployeeDetails(
					letterDetails.stream().map(EmployeeLetterDetails::getEmployeeInfoId).collect(Collectors.toList()));
			for (EmployeeLetterDetails letter : letterDetails) {
				List<EmployeeBasicDetailsDTO> letterEmployee = employeeList.stream()
						.filter(employee -> employee.getEmployeeInfoId().equals(letter.getEmployeeInfoId()))
						.collect(Collectors.toList());
				if (!letterEmployee.isEmpty()) {
					EmployeeBasicDetailsDTO employeeInfo = letterEmployee.get(0);
					letter.getLetters().stream().filter(l -> l.getIsApproved() == null)
							.forEach(pendingLetters -> employeeLetterBasicDTOList.add(EmployeeLetterBasicDTO.builder()
									.employeeInfoId(employeeInfo.getEmployeeInfoId())
									.letterObjectId(letter.getLetterObjectId()).employeeName(employeeInfo.getName())
									.type(pendingLetters.getType()).id(pendingLetters.getId())
									.department(employeeInfo.getDepartment()).designation(employeeInfo.getDesignation())
									.employeeId(employeeInfo.getEmployeeId()).build()));
				}
			}
		}
		return employeeLetterBasicDTOList;
	}

	private List<EmployeeLetterBasicDTO> getApprovedLetters(Long companyId) {
		List<EmployeeLetterBasicDTO> employeeLetterBasicDTOList = new ArrayList<>();
		List<EmployeeLetterDetails> letterDetails = employeeLetterDetailsRepository
				.findByCompanyIdAndLettersIsApproved(companyId, Boolean.TRUE);
		if (!letterDetails.isEmpty()) {
			List<EmployeeBasicDetailsDTO> employeeList = employeePersonalInfoRepository.getEmployeeDetails(
					letterDetails.stream().map(EmployeeLetterDetails::getEmployeeInfoId).collect(Collectors.toList()));
			for (EmployeeLetterDetails letter : letterDetails) {
				List<EmployeeBasicDetailsDTO> letterEmployee = employeeList.stream()
						.filter(employee -> employee.getEmployeeInfoId().equals(letter.getEmployeeInfoId()))
						.collect(Collectors.toList());
				if (!letterEmployee.isEmpty()) {
					EmployeeBasicDetailsDTO employeeInfo = letterEmployee.get(0);
					letter.getLetters().stream()
							.filter(letterInfo -> letterInfo.getIsApproved() != null && letterInfo.getIsApproved())
							.forEach(pendingLetters -> employeeLetterBasicDTOList.add(EmployeeLetterBasicDTO.builder()
									.employeeInfoId(employeeInfo.getEmployeeInfoId())
									.letterObjectId(letter.getLetterObjectId()).employeeName(employeeInfo.getName())
									.type(pendingLetters.getType()).id(pendingLetters.getId())
									.department(employeeInfo.getDepartment()).designation(employeeInfo.getDesignation())
									.employeeId(employeeInfo.getEmployeeId()).build()));
				}
			}
		}
		return employeeLetterBasicDTOList;
	}

	private List<EmployeeLetterBasicDTO> getRejectedLetters(Long companyId) {
		List<EmployeeLetterBasicDTO> employeeLetterBasicDTOList = new ArrayList<>();
		List<EmployeeLetterDetails> letterDetails = employeeLetterDetailsRepository
				.findByCompanyIdAndLettersIsApproved(companyId, Boolean.FALSE);
		if (!letterDetails.isEmpty()) {
			List<EmployeeBasicDetailsDTO> employeeList = employeePersonalInfoRepository.getEmployeeDetails(
					letterDetails.stream().map(EmployeeLetterDetails::getEmployeeInfoId).collect(Collectors.toList()));
			for (EmployeeLetterDetails letter : letterDetails) {
				List<EmployeeBasicDetailsDTO> letterEmployee = employeeList.stream()
						.filter(employee -> employee.getEmployeeInfoId().equals(letter.getEmployeeInfoId()))
						.collect(Collectors.toList());
				if (!letterEmployee.isEmpty()) {
					EmployeeBasicDetailsDTO employeeInfo = letterEmployee.get(0);
					letter.getLetters().stream().filter(l -> l.getIsApproved()!= null && !l.getIsApproved())
							.forEach(pendingLetters -> employeeLetterBasicDTOList
									.add(EmployeeLetterBasicDTO.builder().letterObjectId(letter.getLetterObjectId())
											.employeeInfoId(employeeInfo.getEmployeeInfoId())
											.employeeName(employeeInfo.getName()).type(pendingLetters.getType())
											.id(pendingLetters.getId()).department(employeeInfo.getDepartment())
											.designation(employeeInfo.getDesignation())
											.employeeId(employeeInfo.getEmployeeId()).build()));
				}
			}
		}
		return employeeLetterBasicDTOList;
	}

	@Override
	public EmployeeLetterDTO getLettersById(EmployeeLetterBasicDTO employeeLetterBasicDTO) {
		EmployeeLetterDetails letterDetails = employeeLetterDetailsRepository
				.findById(employeeLetterBasicDTO.getLetterObjectId())
				.orElseThrow(() -> new DataNotFoundException("Letter Details Not Found"));
		EmployeePersonalInfo employee = employeePersonalInfoRepository.findById(letterDetails.getEmployeeInfoId())
				.orElseThrow(() -> new DataNotFoundException("Employee Not Foubd"));
		EmployeeOfficialInfo employeeOfficialInfo = employee.getEmployeeOfficialInfo();
		List<LetterDetails> letters = letterDetails.getLetters();
		if (employeeLetterBasicDTO.getId() - 1 > letters.size()) {
			throw new DataNotFoundException("Letter Details Not Found");
		}
		LetterDetails letter = letters.get(employeeLetterBasicDTO.getId().intValue() - 1);
		EmployeePersonalInfo issuedEmployee = employeePersonalInfoRepository.findById(letter.getIssuedBy())
				.orElseThrow(() -> new DataNotFoundException("Employee Not Foubd"));
		return EmployeeLetterDTO.builder().employeeInfoId(employee.getEmployeeInfoId())
				.employeeName(employee.getFirstName() + " " + employee.getLastName())
				.employeeId(employeeOfficialInfo == null ? null : employeeOfficialInfo.getEmployeeId())
				.department(employeeOfficialInfo == null ? null : employeeOfficialInfo.getDepartment())
				.designation(employeeOfficialInfo == null ? null : employeeOfficialInfo.getDesignation())
				.branchName(employeeOfficialInfo == null ? null
						: (employeeOfficialInfo.getCompanyBranchInfo() == null ? null
								: employeeOfficialInfo.getCompanyBranchInfo().getBranchName()))
				.letterObjectId(letterDetails.getLetterObjectId()).id(letter.getId()).issuedDate(letter.getIssuedDate())
				.type(letter.getType()).url(letter.getUrl()).rejectionReason(letter.getRejectionReason())
				.issuedBy(issuedEmployee.getFirstName() + " " + issuedEmployee.getLastName()).build();
	}

	@Override
	@Transactional
	public Boolean updateStatus(EmployeeLetterDTO employeeLetterDTO, String status) {
		EmployeeLetterDetails letterDetails = employeeLetterDetailsRepository
				.findById(employeeLetterDTO.getLetterObjectId())
				.orElseThrow(() -> new DataNotFoundException("Letter Details Not Found"));
		List<LetterDetails> letters = letterDetails.getLetters();
		if (employeeLetterDTO.getId() - 1 > letters.size()) {
			throw new DataNotFoundException("Letter Details Not Found");
		}
		if (status.equalsIgnoreCase("Rejected")) {
			letters.get(employeeLetterDTO.getId().intValue() - 1).setIsApproved(Boolean.FALSE);
			letters.get(employeeLetterDTO.getId().intValue() - 1)
					.setRejectionReason(employeeLetterDTO.getRejectionReason());
		} else {
			letters.get(employeeLetterDTO.getId().intValue() - 1).setIsApproved(Boolean.TRUE);
		}
		employeeLetterDetailsRepository.save(letterDetails);
		return true;
	}

}
