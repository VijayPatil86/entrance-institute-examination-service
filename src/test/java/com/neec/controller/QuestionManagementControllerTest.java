package com.neec.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neec.dto.QuestionOptionsRequestDTO;
import com.neec.dto.QuestionOptionsResponseDTO;
import com.neec.dto.QuestionRequestDTO;
import com.neec.dto.QuestionResponseDTO;
import com.neec.enums.DifficultyLevel;
import com.neec.service.QuestionAdminService;
import com.neec.util.JwtUtil;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {QuestionManagementController.class})
public class QuestionManagementControllerTest {
	@MockitoBean
	private QuestionAdminService mockQuestionAdminService;
	@MockitoBean
	private JwtUtil mockJwtUtil;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void test_createQuestion_OptionLabel_NotFound() throws Exception {
		List<QuestionOptionsRequestDTO> listQuestionOptionsRequestDTOs =
				List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("1").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("2").build()
				);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("C")
				.options(listQuestionOptionsRequestDTOs)
				.build();
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(questionRequestDTO));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals("Correct option label must match one of the provided option labels", jsonNode.get("correctOptionLabel").asText());
	}

	@Test
	void test_createQuestion_Valid_Data() throws Exception {
		List<QuestionOptionsRequestDTO> listQuestionOptionsRequestDTOs =
				List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("1").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("2").build()
				);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("B")
				.options(listQuestionOptionsRequestDTOs)
				.build();
		List<QuestionOptionsResponseDTO> listQuestionOptionsResponseDTOs =
				List.of(
						QuestionOptionsResponseDTO.builder().optionId(1L).optionLabel("A").optionText("1").build(),
						QuestionOptionsResponseDTO.builder().optionId(2L).optionLabel("B").optionText("2").build()
				);
		QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionId(1L)
				.questionText("What is 2+2?")
				.correctOptionLabel("B")
				.options(listQuestionOptionsResponseDTOs)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();
		when(mockQuestionAdminService.createQuestion(any(QuestionRequestDTO.class)))
			.thenReturn(questionResponseDTO);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/admin/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(questionRequestDTO));
		MvcResult result = mockMvc.perform(request)
				.andDo(print())
				.andReturn();
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		JsonNode jsonNode = toJsonNode(result.getResponse().getContentAsString());
		assertEquals(1L, jsonNode.get("questionId").asLong());
		assertEquals("Maths", jsonNode.get("subject").asText());
		assertEquals("What is 2+2?", jsonNode.get("questionText").asText());
		assertEquals("EASY", jsonNode.get("difficultyLevel").asText());
		assertEquals("B", jsonNode.get("correctOptionLabel").asText());
		assertNotNull(jsonNode.get("createdAt"));
		assertNotNull(jsonNode.get("updatedAt"));

		JsonNode options = jsonNode.get("options");
		assertEquals(2, options.size());
		JsonNode option1 = options.get(0);
		assertEquals(1L, option1.get("optionId").asLong());
		assertEquals("A", option1.get("optionLabel").asText());
		assertEquals("1", option1.get("optionText").asText());
	}

	private String toJsonString(QuestionRequestDTO questionDTO) throws JsonProcessingException {
		return objectMapper.writeValueAsString(questionDTO);
	}

	private JsonNode toJsonNode(String message) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readTree(message);
	}
}
