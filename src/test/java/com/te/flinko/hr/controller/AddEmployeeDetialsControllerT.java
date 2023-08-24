package com.te.flinko.hr.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.te.flinko.controller.hr.AddEmployeeDetialsController;
import com.te.flinko.dto.hr.EmployeeReportingResponseDTO;
import com.te.flinko.dto.hr.GeneralInformationDTO;
import com.te.flinko.dto.hr.ReportingInformationDTO;
import com.te.flinko.dto.hr.WorkInformationDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.hr.EmployeeManagementService;

@SpringBootTest
public class AddEmployeeDetialsControllerT {
	@InjectMocks
	AddEmployeeDetialsController addEmployeeDetialsController;

	MockMvc mockMvc;
	@Mock
	private EmployeeManagementService employeeManagementService;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(addEmployeeDetialsController).build();

	}

	private RequestPostProcessor setHeader(String name, String value) {
		return mockRequest -> {
			mockRequest.addHeader(name, value);
			return mockRequest;
		};
	}

	@Test
	void addGenaralInformation() throws JsonParseException, UnsupportedEncodingException, Exception {

		GeneralInformationDTO generalInformationDTO = new GeneralInformationDTO();

		when(employeeManagementService.addEmployeePersonalInfo(any(), anyLong())).thenReturn(generalInformationDTO);
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/v1/hr/employee/generaldetials")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(generalInformationDTO)))

				.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Employee details updated successfully", readValue.getMessage());

	}

	@Test
	void addWorkInformation() throws JsonParseException, UnsupportedEncodingException, Exception {
		WorkInformationDTO workInformationDTO = new WorkInformationDTO();
		when(employeeManagementService.addWorkInformation(any(), anyLong(), anyLong())).thenReturn(workInformationDTO);
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/v1/hr/employee/workInformation")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(workInformationDTO)))
				.andExpectAll(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Employee details added successfully", readValue.getMessage());

	}

	@Test
	void addReportingInformation() throws JsonParseException, UnsupportedEncodingException, Exception {
		EmployeeReportingResponseDTO employeeReportingResponseDTO = new EmployeeReportingResponseDTO();
		when(employeeManagementService.mapReportingInformation(any())).thenReturn(employeeReportingResponseDTO);
		 String contentAsString = mockMvc.perform(post("/api/v1/hr/employee/reportingdetails").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeReportingResponseDTO))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
        assertEquals("Employee details added successfully", readValue.getMessage());
	}
}
