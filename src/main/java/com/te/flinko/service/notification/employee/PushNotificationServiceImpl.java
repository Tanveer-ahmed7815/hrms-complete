package com.te.flinko.service.notification.employee;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.employee.EmployeeExpoTokenDTO;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.employee.InvalidTokenException;
import com.te.flinko.exception.employee.PushNotificationException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientCustomData;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Transactional
	@Override
	public Boolean updateExpoToken(EmployeeExpoTokenDTO employeeExpoTokenDTO) {
		EmployeePersonalInfo employee = employeePersonalInfoRepository
				.findById(employeeExpoTokenDTO.getEmployeeInfoId())
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		employee.setExpoToken(employeeExpoTokenDTO.getExpoToken());
		employeePersonalInfoRepository.save(employee);
		return Boolean.TRUE;
	}

	public void pushMessage(String title, String message, String recipient) {
		try {
			if (!PushClientCustomData.isExponentPushToken(recipient))
				throw new InvalidTokenException("Token:" + recipient + " is not a valid token.");

			ExpoPushMessage expoPushMessage = new ExpoPushMessage();
			expoPushMessage.getTo().add(recipient);
			expoPushMessage.setTitle(title);
			expoPushMessage.setBody(message);

			List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
			expoPushMessages.add(expoPushMessage);

			PushClient client = new PushClient();
			List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

			for (List<ExpoPushMessage> chunk : chunks) {
				client.sendPushNotificationsAsync(chunk);
			}

		} catch (Exception exception) {
			throw new PushNotificationException(exception.getMessage());
		}
	}
}
