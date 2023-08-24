package com.te.flinko.service.helpandsupport.socket.mongo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.te.flinko.dto.helpandsupport.mongo.CompanyChatDetailsDTO;
import com.te.flinko.service.helpandsupport.mongo.CompanyChatDetailsService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketModule {

	private final GenerateHashCode generateHashCode;

	@SuppressWarnings("unused")
	private final SocketIOServer server;
	private final SocketService socketService;
	private final CompanyChatDetailsService companyChatDetailsService;

	public SocketModule(SocketIOServer server, SocketService socketService, GenerateHashCode generateHashCode,
			CompanyChatDetailsService companyChatDetailsService) {
		this.server = server;
		this.socketService = socketService;
		this.generateHashCode = generateHashCode;
		this.companyChatDetailsService = companyChatDetailsService;
		server.addConnectListener(onConnected());
		server.addDisconnectListener(onDisconnected());
		server.addEventListener("send_message", CompanyChatDetailsDTO.class, onChatReceived());

	}

	private DataListener<CompanyChatDetailsDTO> onChatReceived() {
		return (senderClient, data, ackSender) -> {
			log.info(data.toString());
			socketService.saveMessage(senderClient, data);
		};
	}

	private ConnectListener onConnected() {
		return client -> {
			Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
			String compnayId = params.get("compnayId").stream().collect(Collectors.joining());
			String senderEmployeeId = params.get("senderEmployeeId").stream().collect(Collectors.joining());
			String senderEmployeeInfoId = params.get("senderEmployeeInfoId").stream().collect(Collectors.joining());
			String receiverEmployeeId = params.get("receiverEmployeeId").stream().collect(Collectors.joining());
			String receiverEmployeeInfoId = params.get("receiverEmployeeInfoId").stream().collect(Collectors.joining());

			String room = generateHashCode.hashCode(compnayId, senderEmployeeId, senderEmployeeInfoId,
					receiverEmployeeId, receiverEmployeeInfoId);
			client.joinRoom(room);
			log.info("Socket ID[{}] - room[{}] - username [{}]  Connected to chat module through",
					client.getSessionId().toString(), room, senderEmployeeId);
		};

	}

	private DisconnectListener onDisconnected() {
		return client -> {
			log.info("Socket ID[{}] - room[{}] - username [{}]  discnnected to chat module through",
					client.getSessionId().toString());
			client.disconnect();
		};

	}
}
