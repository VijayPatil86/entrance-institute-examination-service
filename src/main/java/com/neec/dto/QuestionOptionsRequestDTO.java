package com.neec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
public class QuestionOptionsRequestDTO {
	@NotBlank(message = "Option Label can not be blank")
	@Pattern(regexp = "^[A-Z]$", message = "Option Label must be a single uppercase letter (e.g., A, B, C)")
	String optionLabel;

	@NotBlank(message = "Option Text can not be blank")
	String optionText;
}
