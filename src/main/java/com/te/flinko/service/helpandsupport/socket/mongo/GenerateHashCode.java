package com.te.flinko.service.helpandsupport.socket.mongo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenerateHashCode {
	public String hashCode(String compnayId ,String senderEmployeeId ,String  senderEmployeeInfoId ,String  receiverEmployeeId
			,String  receiverEmployeeInfoId) {
		 String room = List.of(compnayId,senderEmployeeId,receiverEmployeeId).stream().sorted().collect(Collectors.joining());
		log.info("room number {}", room);
		return room;
	}
}
