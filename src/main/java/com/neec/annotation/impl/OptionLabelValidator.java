package com.neec.annotation.impl;

import java.util.List;
import java.util.Objects;

import com.neec.annotation.DuplicateOptionLabel;
import com.neec.dto.QuestionOptionsRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionLabelValidator implements ConstraintValidator<DuplicateOptionLabel, List<QuestionOptionsRequestDTO>> {
	@Override
	public boolean isValid(List<QuestionOptionsRequestDTO> options, ConstraintValidatorContext context) {
		if(options == null) {
			return true;	// Let @NotNull handle null check
		}
		long uniqueOptionsLabelsCount = options.stream()
				.map(dto -> dto.getOptionLabel())
				.filter(Objects::nonNull)
				.distinct()
				.count();
		boolean isOptionsLabelsCountMatch = (options.size() == uniqueOptionsLabelsCount);
		if(!isOptionsLabelsCountMatch) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid options: option labels must be unique")
				.addPropertyNode("options")
				.addConstraintViolation();
		}
		return isOptionsLabelsCountMatch;
	}
}
