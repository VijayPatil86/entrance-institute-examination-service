package com.neec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
public class SubmitAnswerRequestDTO {
	@NotNull(message = "Question ID cannot be null.")
	Long questionId;

	@NotNull(message = "Selected option ID cannot be null.")
	Long selectedOptionId;
}
