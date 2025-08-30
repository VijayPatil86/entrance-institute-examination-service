package com.neec.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class QuestionOptionsRequestDTOTest {
	private Validator validator;

	@BeforeEach
	void setup() {
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	// optionLabel: @NotBlank @Pattern
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "  ", "AA", "a", "aa", "@", "$$"})
	void test_optionLabel_Invalid(String input) {
		QuestionOptionsRequestDTO dto = QuestionOptionsRequestDTO.builder()
				.optionLabel(input)
				.build();
		Set<ConstraintViolation<QuestionOptionsRequestDTO>> violations =
				validator.validateProperty(dto, "optionLabel");
		assertTrue(!violations.isEmpty(), "optionLabel: Expected violation message(s)");
	}

	// questionText: @NotBlank
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	void test_questionText_Invalid(String input) {
		QuestionOptionsRequestDTO dto = QuestionOptionsRequestDTO.builder()
				.optionText(input)
				.build();
		Set<ConstraintViolation<QuestionOptionsRequestDTO>> violations =
				validator.validateProperty(dto, "optionText");
		assertTrue(!violations.isEmpty(), "optionText: Expected violation message");
	}

	@Test
	void test_all_fields_valid() {
		QuestionOptionsRequestDTO dto = QuestionOptionsRequestDTO.builder()
				.optionLabel("A")
				.optionText("4")
				.build();
		Set<ConstraintViolation<QuestionOptionsRequestDTO>> violations =
				validator.validate(dto);
		assertTrue(violations.isEmpty(), "valid dto should not have violation");
	}
}
