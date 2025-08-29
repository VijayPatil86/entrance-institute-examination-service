package com.neec.dto;

import java.util.List;

import com.neec.annotation.DuplicateOptionLabel;
import com.neec.annotation.OptionLabelMatch;
import com.neec.enums.DifficultyLevel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@OptionLabelMatch
public class QuestionRequestDTO {
	@NotBlank(message = "Question text can not be blank")
	String questionText;

	@NotBlank(message = "Question Subject can not be blank")
	String subject;

	@NotNull(message = "Question Difficulty Level can not be blank")
	DifficultyLevel difficultyLevel;

	@NotBlank(message = "Correct Option Label can not be blank")
	@Pattern(regexp = "^[A-Z]$", message = "Correct Option Label must be a single uppercase letter (e.g., A, B, C)")
	String correctOptionLabel;	// "A", "B", "C"

	@NotNull(message = "Options can not be blank")
	@Size(min = 2, message = "A Question must have at least 2 options")
	@DuplicateOptionLabel(message = "Options Labels must not be duplicate")
	List<@Valid QuestionOptionsRequestDTO> options;
}
