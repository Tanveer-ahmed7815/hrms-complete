package com.te.flinko.service.mail.employee;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.employee.MailDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@javax.annotation.ParametersAreNonnullByDefault
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Value("${spring.mail.username}")
	private String from;

	public Integer sendMail(MailDto mailDto) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setSubject(mailDto.getSubject());
			helper.setText(mailDto.getBody());
			helper.setTo(mailDto.getTo());
			emailSender.send(message);
			return HttpStatus.OK.value();
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("www.fast2sms.com with error :", exception.getMessage());
			return 500;			
		}

	}
	public Integer sendMailToContactUs(MailDto mailDto) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setSubject(mailDto.getSubject());
			helper.setText(mailDto.getBody(),true);
			helper.setTo(mailDto.getTo());
			emailSender.send(message);
			return HttpStatus.OK.value();
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("www.fast2sms.com with error :", exception.getMessage());
			return 500;			
		}

	}
	public Integer sendMailWithLink(MailDto mailDto) {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
			message.setContent(mailDto.getBody(), "text/html");
			helper.setTo(mailDto.getTo());
			helper.setSubject(mailDto.getSubject());
			emailSender.send(message);
			return HttpStatus.OK.value();
		} catch (javax.mail.MessagingException exception) {
			exception.printStackTrace();
			log.error("www.fast2sms.com with error :", exception.getMessage());
			return 500;	
		}

	}

	public Integer sendMailWithAttachment(String mailDto, MultipartFile multipartFile) {
		try {
			MailDto mail = BeanCopy.jsonProperties(mailDto, MailDto.class);
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setSubject(mail.getSubject());
			helper.setText(mail.getBody());
			helper.setTo(mail.getTo());
			helper.addAttachment("" + multipartFile.getOriginalFilename(), multipartFile);
			emailSender.send(message);
			return HttpStatus.OK.value();
		} catch (javax.mail.MessagingException exception) {
			exception.printStackTrace();
			log.error("www.fast2sms.com with error :", exception.getMessage());
			return 500;	
		}

	}

}