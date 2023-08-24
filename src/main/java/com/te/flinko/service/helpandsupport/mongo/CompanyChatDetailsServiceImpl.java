package com.te.flinko.service.helpandsupport.mongo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.te.flinko.audit.common.db.DBConstants;
import com.te.flinko.dto.helpandsupport.mongo.CompanyChatDetailsDTO;
import com.te.flinko.dto.helpandsupport.mongo.Conversation;
import com.te.flinko.entity.helpandsupport.mongo.CompanyChatDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.helpandsupport.mongo.CompanyChatDetailsRepository;
import com.te.flinko.util.Generator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyChatDetailsServiceImpl implements CompanyChatDetailsService {

	private final CompanyChatDetailsRepository companyChatDetailsRepository;

	private final Generator generator;

	public Map<LocalDate, List<CompanyChatDetailsDTO>> getAllChatMessage(Long companyId, String employeeIdOne,
			String employeeIdTwo) {
		return companyChatDetailsRepository
				.findByCompanyIdAndEmployeeOneInAndEmployeeTwoIn(companyId, List.of(employeeIdOne, employeeIdTwo),
						List.of(employeeIdTwo, employeeIdOne))
				.map(chat -> chat.getConversations().stream()
						.map(x -> CompanyChatDetailsDTO.builder().employeeId(x.getSenderEmployeeId())
								.contant(x.getContant()).sendTime(x.getDate().toLocalTime())
								.sendDate(x.getDate().toLocalDate()).read(x.getRead()).build())
						.collect(Collectors.groupingBy(CompanyChatDetailsDTO::getSendDate, TreeMap::new,
								Collectors.mapping(xyz -> xyz, Collectors.toList()))))
				.orElseGet(TreeMap::new);
	}

	@Override
	public CompanyChatDetails sendMessage(Long companyId, String employeeIdOne,
			CompanyChatDetailsDTO companyChatDetailsDTO) {
		if (Objects.equal(employeeIdOne, companyChatDetailsDTO.getEmployeeId()))
			throw new DataNotFoundException("Can Not Send Message To Yourself");

		return companyChatDetailsRepository.findByCompanyIdAndEmployeeOneInAndEmployeeTwoIn(companyId,
				List.of(employeeIdOne, companyChatDetailsDTO.getEmployeeId()),
				List.of(companyChatDetailsDTO.getEmployeeId(), employeeIdOne)).map(chat -> {
					chat.getConversations()
							.add(Conversation.builder().senderEmployeeId(employeeIdOne).date(LocalDateTime.now())
									.contant(companyChatDetailsDTO.getContant()).read(companyChatDetailsDTO.getRead())
									.build());
					return companyChatDetailsRepository.save(chat);
				})
				.orElseGet(() -> companyChatDetailsRepository.save(CompanyChatDetails.builder()
						.chatId(generator.generateSequence(DBConstants.COMPANY_CHAT_DETAILS_SEQUENCE_NAME))
						.companyId(companyId).employeeOne(employeeIdOne).room(companyChatDetailsDTO.getRoom())
						.employeeTwo(companyChatDetailsDTO.getEmployeeId())
						.conversations(List.of(Conversation.builder().senderEmployeeId(employeeIdOne)
								.date(LocalDateTime.now()).contant(companyChatDetailsDTO.getContant())
								.read(companyChatDetailsDTO.getRead()).build()))
						.build()));
	}

}
