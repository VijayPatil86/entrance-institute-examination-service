package com.neec.dto;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class SubmitAnswerResponseDTO {
	Long answerId;
	Long questionId;
	Long selectedOptionId;
	Boolean isCorrectAnswer;
	Instant questionSubmittedAt;
}
