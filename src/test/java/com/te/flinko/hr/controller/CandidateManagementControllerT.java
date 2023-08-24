package com.te.flinko.hr.controller;

import static com.te.flinko.common.hr.HrConstants.CANDIDATE_INFORMATION_UPDATED_SUCCESSFULLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.te.flinko.controller.hr.CandidateManagementController;
import com.te.flinko.dto.employee.EmployeeDropdownDTO;
import com.te.flinko.dto.hr.CandidateInfoDTO;
import com.te.flinko.dto.hr.CandidateInfoDTOById;
import com.te.flinko.dto.hr.CandidateListDTO;
import com.te.flinko.dto.hr.FollowUpDTO;
import com.te.flinko.dto.hr.FollowUpDetailsDTO;
import com.te.flinko.dto.hr.InterviewFeedbackInfoDTO;
import com.te.flinko.dto.hr.RejectedCandidatedetailsDTO;
import com.te.flinko.dto.hr.ScheduledCandidateDTO;
import com.te.flinko.dto.hr.ScheduledCandidateDetailsDTO;
import com.te.flinko.dto.hr.SendLinkDTO;
import com.te.flinko.dto.hr.UpdateFeedbackDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.hr.CandidateManagementService;

@SpringBootTest
public class CandidateManagementControllerT {

	@LocalServerPort
	HttpHeaders httpHeaders = new HttpHeaders();
	
	
	@InjectMocks
	CandidateManagementController candidateManagementController;

	MockMvc mockMvc;

	@Mock
	private CandidateManagementService candidateManagementService;

	ObjectMapper objectMapper = new ObjectMapper();
	
	

	

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(candidateManagementController).build();
	}

	@BeforeEach
	public void setUp1() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeader("companyId")).thenReturn("1054");
		Mockito.when(request.getHeader("userId")).thenReturn("1039");
	}

	@Test
	void newCandidate() throws JsonProcessingException, UnsupportedEncodingException, Exception {

		CandidateInfoDTO candidateInfoDTO = new CandidateInfoDTO();

		when(candidateManagementService.newCandidate(any())).thenReturn(candidateInfoDTO);
		String contentAsString = mockMvc
				.perform(post("/api/v1/candidate/newCandidate").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(candidateInfoDTO)))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);

		assertEquals("New candidate added successfully", readValue.getMessage());

	}

	@Test
	void editCandidateInfo() throws JsonProcessingException, UnsupportedEncodingException, Exception {
		CandidateInfoDTO candidateInfoDTO = new CandidateInfoDTO();
		candidateInfoDTO.setAverageGrade("a");
		candidateInfoDTO.setCompanyId(1054l);
		candidateInfoDTO.setCourse("Bcs");
		candidateInfoDTO.setDepartmentId(1l);
		candidateInfoDTO.setDesignationName("backend developer");
		candidateInfoDTO.setEmailId("rrr@mailinator.com");
		candidateInfoDTO.setFirstName("abc");
		candidateInfoDTO.setHighestDegree("Mcs");
		candidateInfoDTO.setInstituteName("mit College");
		candidateInfoDTO.setLastName("cba");
		candidateInfoDTO.setMobileNumber(1234566541l);
		when(candidateManagementService.editCandidateInfo(any())).thenReturn(candidateInfoDTO);
		String contentAsString = mockMvc
				.perform(post("/api/v1/candidate/editCandidateInfo").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(candidateInfoDTO)))
				.andExpect(status().isAccepted()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals(CANDIDATE_INFORMATION_UPDATED_SUCCESSFULLY, readValue.getMessage());
	}

	@Test
	void deleteCandidateInfo() throws JsonProcessingException, UnsupportedEncodingException, Exception {
		when(candidateManagementService.deleteCandidateInfo(anyLong(), anyLong())).thenReturn("");
		String contentAsString = mockMvc
				.perform(delete("/api/v1/candidate/deleteCandidateInfo").param("candidateId", "7").param("companyId",
						"1039"))

				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate details deleted successfully ", readValue.getMessage());
	}

	@Test
	void showData() throws JsonProcessingException, UnsupportedEncodingException, Exception {
		List<CandidateListDTO> arrayList = new ArrayList<>();
		CandidateListDTO candidateListDTO = new CandidateListDTO();
		candidateListDTO.setCandidateId(1l);
		arrayList.add(candidateListDTO);
		candidateListDTO.setDesignationName("se");
		when(candidateManagementService.candidateDetailsList(anyLong())).thenReturn(arrayList);
		String contentAsString = mockMvc
				.perform(get("/api/v1/candidate/candidateDetailsList").param("companyId", "1054"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());
	}

	@Test
	void findCandidateInfoByUsingId() throws JsonProcessingException, UnsupportedEncodingException, Exception {
		when(candidateManagementService.findCandidateInfoByUsingId(anyLong(), anyLong()))
				.thenReturn(new CandidateInfoDTOById());
		String contentAsString = mockMvc
				.perform(get("/api/v1/candidate/findCandidateInfoByUsingId").param("candidateId", "1")
						.param("companyId", "123"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());

	}

	@Test
	void followUp() throws JsonProcessingException, UnsupportedEncodingException, Exception {
		ArrayList<FollowUpDTO> arrayList = new ArrayList<>();
		FollowUpDTO followUpDTO = new FollowUpDTO();
		followUpDTO.setDesignationName("tds");
		arrayList.add(followUpDTO);

		when(candidateManagementService.followUp(anyLong())).thenReturn(arrayList);
		String contentAsString = mockMvc.perform(get("/api/v1/candidate/followUp").param("companyId", "1045"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());
	}

	@Test
	void followUpDetails() throws JsonParseException, UnsupportedEncodingException, Exception {
		FollowUpDetailsDTO followUpDetailsDTO = new FollowUpDetailsDTO();
		when(candidateManagementService.followUpDetails(anyLong(), anyLong())).thenReturn(followUpDetailsDTO);
		String contentAsString = mockMvc
				.perform(get("/api/v1/candidate/followUpDetails").param("candidateId", "10").param("companyId", "1054"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());

	}

	@Test
	void scheduledCandidates() throws JsonParseException, UnsupportedEncodingException, Exception {
		ArrayList<ScheduledCandidateDTO> arrayList = new ArrayList<>();
		ScheduledCandidateDTO scheduledCandidateDTO = new ScheduledCandidateDTO();
		scheduledCandidateDTO.setFirstName("ravi");
		arrayList.add(scheduledCandidateDTO);

		when(candidateManagementService.scheduledCandidates(anyLong())).thenReturn(arrayList);
		String contentAsString = mockMvc
				.perform(get("/api/v1/candidate/scheduledCandidates").param("companyId", "1054")).andReturn()
				.getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());

	}

	@Test
	void rejectedCandidates() throws JsonParseException, UnsupportedEncodingException, Exception {
		ArrayList<FollowUpDTO> rejected = new ArrayList<>();
		FollowUpDTO followUpDTO = new FollowUpDTO();
		rejected.add(followUpDTO);
		when(candidateManagementService.rejectedCandidates(anyLong())).thenReturn(rejected);
		String contentAsString = mockMvc.perform(get("/api/v1/candidate/rejectedCandidates").param("companyId", "1012"))
				.andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Rejected candidate records Fetched successfully", readValue.getMessage());

	}

	@Test
	void scheduledCandidateDetails() throws JsonParseException, UnsupportedEncodingException, Exception {
		when(candidateManagementService.scheduledCandidateDetails(anyLong(), anyLong()))
				.thenReturn(new ScheduledCandidateDetailsDTO());
		String contentAsString = mockMvc.perform(get("/api/v1/candidate/scheduledCandidateDetails")
				.param("companyId", "121").param("candidateId", "123")).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());

	}

	@Test
	void rejectedCandidateDetails() throws UnsupportedEncodingException, JsonParseException, Exception {
		when(candidateManagementService.rejectedCandidateDetails(anyLong(), anyLong()))
				.thenReturn(new RejectedCandidatedetailsDTO());
		String contentAsString = mockMvc.perform(
				get("/api/v1/candidate/rejectedCandidateDetails").param("candidateId", "1").param("companyId", "1"))
				.andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate record Fetched successfully", readValue.getMessage());
	}

	@Test
	void updateFeedback() throws UnsupportedEncodingException, JsonParseException, Exception {
		UpdateFeedbackDTO dto = new UpdateFeedbackDTO();
		when(candidateManagementService.updateFeedback(anyLong(), any())).thenReturn(dto);
		String contentAsString = mockMvc
				.perform(post("/api/v1/candidate/updateFeedback/1").param("interviewId", "1")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Candidate interview feedback updated successfully", readValue.getMessage());
	}

	@Test
	void employeeDropdownList() throws UnsupportedEncodingException, JsonParseException, Exception {
		ArrayList<EmployeeDropdownDTO> al = new ArrayList<>();
		EmployeeDropdownDTO employeeDropdownDTO = new EmployeeDropdownDTO();
		al.add(employeeDropdownDTO);
		when(candidateManagementService.employeeDropdownList(anyLong())).thenReturn(any());
		String contentAsString = mockMvc.perform(get("/api/v1/candidate/employee-list").param("companyId", "123"))
				.andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals("Employee Details Fetched Successfully", readValue.getMessage());
	}

	@Test
	void interviewFeedbackInfo() throws UnsupportedEncodingException, JsonParseException, Exception {
		InterviewFeedbackInfoDTO interviewFeedbackInfoDTO = new InterviewFeedbackInfoDTO();
		when(candidateManagementService.interviewFeedbackInfo(anyLong(), anyLong()))
				.thenReturn(interviewFeedbackInfoDTO);
		String contentAsString = mockMvc.perform(
				get("/api/v1/candidate/interview-Feedback-Info").param("companyId", "12").param("interviewId", "1"))
				.andReturn().getResponse().getContentAsString();
		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
		assertEquals(" Candidate interview feedback details fetch successfully", readValue.getMessage());

	}

//	@Test
//	void sendLink() throws JsonParseException, UnsupportedEncodingException, Exception {
//		SendLinkDTO sendLinkDTO = new SendLinkDTO();
//		sendLinkDTO.setCandidateId(1l);
//		sendLinkDTO.setLink("www.v.com");
//		
//		when(candidateManagementService.sendLink(sendLinkDTO.getLink(), sendLinkDTO.getCandidateId(), 1054l, 1l))
//				.thenReturn("link send successfully");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String requestJson = objectMapper.writeValueAsString(sendLinkDTO);
//
//        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
//        servletRequest.addHeader("companyId", "1054");
//        servletRequest.addHeader("userId", "1223");
//        
//        
//		String contentAsString = mockMvc
//				.perform(MockMvcRequestBuilders.post("/api/v1/candidate/send-link").with(setHeader("companyId", "1054"))
//						.with(setHeader("userId", "1223")).accept(MediaType.APPLICATION_JSON)
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(requestJson))
//
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
//
//		SuccessResponse readValue = objectMapper.readValue(contentAsString, SuccessResponse.class);
//		assertEquals(" Link Sent Successfully", readValue.getMessage());
//
//	}
//
//	private RequestPostProcessor setHeader(String name, String value) {
//		return mockRequest -> {
//			mockRequest.addHeader(name, value);
//			return mockRequest;
//		};
//	}
	@Test
	void sendLink() throws JsonParseException, UnsupportedEncodingException, Exception {
		  HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
		  
		SendLinkDTO sendLinkDTO = new SendLinkDTO();
		sendLinkDTO.setCandidateId(1l); 
		sendLinkDTO.setLink("www.v.com");
		CandidateManagementController candidateManagementController2 = new CandidateManagementController();
		when(candidateManagementService.sendLink("www.com", 12121l, 1054l, 1l)).thenReturn("link send successfully");
		String requestJson = objectMapper.writeValueAsString(sendLinkDTO);
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/v1/candidate/send-link").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(requestJson))

				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

	}
}