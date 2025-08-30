package com.neec.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.neec.enums.DifficultyLevel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class QuestionRequestDTOTest {
	private Validator validator;

	@BeforeEach
	void setup() {
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	// questionText: @NotBlank
	@Test
	void test_questionText_Null() {
		QuestionRequestDTO dto = QuestionRequestDTO.builder()
				.questionText(null)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(dto, "questionText");
		assertTrue(!violations.isEmpty(), "questionText: Expected violation message");
	}

	@Test
	void test_questionText_Blank() {
		QuestionRequestDTO dto = QuestionRequestDTO.builder()
				.questionText("")
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(dto, "questionText");
		assertTrue(!violations.isEmpty(), "questionText: Expected violation message");
	}

	// subject: @NotBlank, skipped

	// difficultyLevel: @NotNull
	@Test
	void test_difficultyLevel_Null() {
		QuestionRequestDTO dto = QuestionRequestDTO.builder()
				.difficultyLevel(null)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(dto, "difficultyLevel");
		assertTrue(!violations.isEmpty(), "difficultyLevel: Expected violation message");
	}

	// correctOptionLabel: @NotBlank @Pattern
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "  ", "AA", "a", "aa", "@", "$$"})
	void test_correctOptionLabel_Invalid(String input) {
		QuestionRequestDTO dto = QuestionRequestDTO.builder()
				.correctOptionLabel(input)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(dto, "correctOptionLabel");
		assertTrue(!violations.isEmpty(), "correctOptionLabel: Expected violation message(s)");
	}

	// options: @NotNull @Size @DuplicateOptionLabel
	@Test
	void test_options_Null() {
		QuestionRequestDTO dto = QuestionRequestDTO.builder()
				.options(null)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(dto, "options");
		assertTrue(!violations.isEmpty());
	}

	@Test
	void test_options_one_option() {
		QuestionOptionsRequestDTO questionOptionsRequestDTO =
				QuestionOptionsRequestDTO.builder().build();
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.options(List.of(questionOptionsRequestDTO))
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(questionRequestDTO, "options");
		assertTrue(!violations.isEmpty());
	}

	@Test
	void test_options_duplicate_option_label() {
		QuestionOptionsRequestDTO questionOptionsRequestDTO_Label_A =
				QuestionOptionsRequestDTO.builder()
					.optionLabel("A")
					.build();
		QuestionOptionsRequestDTO questionOptionsRequestDTO_Label_Duplicate_A =
				QuestionOptionsRequestDTO.builder()
					.optionLabel("A")
					.build();
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.options(List.of(questionOptionsRequestDTO_Label_A, questionOptionsRequestDTO_Label_Duplicate_A))
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validateProperty(questionRequestDTO, "options");
		assertTrue(!violations.isEmpty());
	}

	// valid
	@Test
	void test_all_fields_valid() {
		List<QuestionOptionsRequestDTO> listQuestionOptionsRequestDTOs =
				List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("4").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("5").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("C").optionText("6").build()
				);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("A")
				.options(listQuestionOptionsRequestDTOs)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validate(questionRequestDTO);
		assertTrue(violations.isEmpty(), "A valid DTO should not have no violations");
	}

	// option label mismatch
	@Test
	void test_option_label_mismatch() {
		List<QuestionOptionsRequestDTO> listQuestionOptionsRequestDTOs =
				List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("4").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("5").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("C").optionText("6").build()
				);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("D")
				.options(listQuestionOptionsRequestDTOs)
				.build();
		Set<ConstraintViolation<QuestionRequestDTO>> violations =
				validator.validate(questionRequestDTO);
		assertTrue(!violations.isEmpty(), "Expected label mismatch violation");
	}
}
