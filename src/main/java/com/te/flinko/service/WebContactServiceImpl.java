package com.te.flinko.service;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.WebContactDto;
import com.te.flinko.dto.employee.MailDto;
import com.te.flinko.service.mail.employee.EmailService;

@Service
public class WebContactServiceImpl implements WebContactService {

	@Autowired
	private EmailService emailService;

	@Override
	public String webContact(WebContactDto webContactDto) {
		MailDto mailDto = new MailDto();
		mailDto.setBody(

				"Hello " + webContactDto.getEmployeeName() + "<BR >" + "<BR>"

						+ "Thank you for reaching out to us." + "<BR >"
						+ "We value your interest in Flinko and will get back to you at the earliest." + "<BR >" +

						"Please check your email inbox for further communication." + "<BR >"
						+ "If you need immediate assistance or have any further questions, feel free to write us at mail to:flinko.app@gmail.com"
						+ "<BR>" + "<BR>" +"<BR>" +
						"Yours sincerely," + "<BR >"
						 + "Team Flinko");
		mailDto.setSubject("Thank you for contacting Flinko");
		mailDto.setTo(webContactDto.getEmail());
		emailService.sendMailToContactUs(mailDto);
		
		LocalDate localDate = new LocalDate();
		LocalTime localTime = new LocalTime();
		MailDto mailToSelf = new MailDto();
		mailToSelf.setTo("flinko.app@gmail.com");
		mailToSelf.setBody( 

				"Dear team," + "<BR>" +"<BR>" +

				"We have received a contact request from " + webContactDto.getCompanyName() + " on " + localDate.now()
				+ "  at  " + localTime.getHourOfDay() + ":" + localTime.getSecondOfMinute() + ":"
				+ localTime.getMillisOfSecond() + "<BR>"+ "<BR>"
				+ " Please find the user details below." + "<BR>" + "<BR>"  +

				"Details:" + "<BR>" +

				"Company Name :" + webContactDto.getCompanyName() + "<BR>" +

				"Employee Name :" + webContactDto.getEmployeeName() + "<BR>" +

				"Email ID :" + webContactDto.getEmail() + "<BR>" +

				"Contact details:" + webContactDto.getMobileNo() + "<BR>" +

				"Message :" + webContactDto.getMessage() + "<BR>" +"<BR>" +

				"Note : You need to communicate with the customer through call or email." + "<BR>" + "<BR>" +

				"Yours sincerely," + "<BR>"

				+ "Finko Team" + "</html>");

		mailToSelf.setSubject("Contact customer request"); 

		emailService.sendMailToContactUs(mailToSelf);
		return "mail send successfully";

	}

}
