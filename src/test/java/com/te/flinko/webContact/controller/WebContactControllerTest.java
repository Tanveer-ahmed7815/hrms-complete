package com.te.flinko.webContact.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.te.flinko.controller.WebContactController;
import com.te.flinko.dto.WebContactDto;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.WebContactService;


@SpringBootTest
public class WebContactControllerTest {
	@InjectMocks
	WebContactController contactController;

	MockMvc mockMvc;
	@Mock
	private WebContactService webContactService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
	}
	@Test
	void webContact() throws JsonProcessingException, UnsupportedEncodingException, Exception {
	
		WebContactDto webContactDto = new WebContactDto();
		webContactDto.setCompanyName("TestYantra");
		webContactDto.setEmployeeName("Ravbgi");
		webContactDto.setEmail("sss123@mailinator.com");
		webContactDto.setMessage("kk");
		webContactDto.setMobileNo(8789878978l);
		
		String s = "mail send successfully";
		when(webContactService.webContact(webContactDto)).thenReturn(s);
		System.err.println("s is : "+s);
		String contentAsString = mockMvc
				.perform(post("/api/v1/web-contact").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(webContactDto)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				System.err.println("contentAsString is : "+contentAsString);
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("response added successfully",readValue.getMessage());
		

	}
	void followUp() {
		
	}

}
