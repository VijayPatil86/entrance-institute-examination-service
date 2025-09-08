package com.neec.dto;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for representing a student's final exam result.
 */
@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class ExamResultDTO {
	Long userId;
	int score;
	int totalQuestions;
	int correctAnswers;
	int incorrectAnswers;
	int rank;
	Instant resultPublishDate;
}
