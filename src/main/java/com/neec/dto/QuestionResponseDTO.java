package com.neec.dto;

import java.time.Instant;
import java.util.List;

import com.neec.enums.DifficultyLevel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class QuestionResponseDTO {
	Long questionId;
	String questionText;
	String subject;
	DifficultyLevel difficultyLevel;
	String correctOptionLabel;	// "A", "B", "C"
	List<QuestionOptionsResponseDTO> options;
	Instant createdAt;
	Instant updatedAt;
}
