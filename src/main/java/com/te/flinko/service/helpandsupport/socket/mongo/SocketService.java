package com.te.flinko.service.helpandsupport.socket.mongo;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.te.flinko.dto.helpandsupport.mongo.CompanyChatDetailsDTO;
import com.te.flinko.service.helpandsupport.mongo.CompanyChatDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {
	private final GenerateHashCode generateHashCode;

	private final CompanyChatDetailsService companyChatDetailsService;

	public void sendSocketMessage(SocketIOClient senderClient, CompanyChatDetailsDTO companyChatDetails, String room) {
		for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
			if (!client.getSessionId().equals(senderClient.getSessionId())) {
				client.sendEvent("read_message", companyChatDetails);
			}
		}
	}

	public void saveMessage(SocketIOClient senderClient, CompanyChatDetailsDTO message) {

		String room = generateHashCode.hashCode(message.getCompnayId(), message.getSenderEmployeeId(),
				message.getSenderEmployeeInfoId(), message.getReceiverEmployeeId(),
				message.getReceiverEmployeeInfoId());

		log.info("message {} room {}", message, room);

		companyChatDetailsService.sendMessage(Long.parseLong(message.getCompnayId()), message.getSenderEmployeeId(),
				CompanyChatDetailsDTO.builder().employeeId(message.getReceiverEmployeeId())
						.contant(message.getContant()).room(room).read(Boolean.FALSE).build());

		sendSocketMessage(senderClient, CompanyChatDetailsDTO.builder().employeeId(message.getSenderEmployeeId())
				.contant(message.getContant()).read(Boolean.FALSE).build(), room);
	}
}