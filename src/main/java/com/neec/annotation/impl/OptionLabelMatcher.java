package com.neec.annotation.impl;

import java.util.List;
import java.util.Objects;

import com.neec.annotation.OptionLabelMatch;
import com.neec.dto.QuestionOptionsRequestDTO;
import com.neec.dto.QuestionRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionLabelMatcher implements ConstraintValidator<OptionLabelMatch, QuestionRequestDTO> {
	@Override
	public boolean isValid(QuestionRequestDTO dto, ConstraintValidatorContext context) {
		if(dto == null) {
			return true;
		}
		String correctOptionLabel = dto.getCorrectOptionLabel();
		List<QuestionOptionsRequestDTO> options = dto.getOptions();
		if(correctOptionLabel == null || options == null || options.isEmpty()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Correct option label must not be null and must match one of the provided options")
				.addPropertyNode("correctOptionLabel")
				.addConstraintViolation();
			return false;
		}
		boolean optionLabelMatch = options.stream()
			.map(option -> option.getOptionLabel())
			.filter(Objects::nonNull)
			.anyMatch(label -> correctOptionLabel.equals(label));
		if(!optionLabelMatch) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Correct option label must match one of the provided option labels")
				.addPropertyNode("correctOptionLabel")
				.addConstraintViolation();
		}
		return optionLabelMatch;
	}
}
