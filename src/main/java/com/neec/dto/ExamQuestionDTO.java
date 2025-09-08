package com.neec.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for representing a question to a student during an exam.
 * IMPORTANT: This DTO deliberately omits any information about the correct answer.
 */
@Builder @AllArgsConstructor @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter
public class ExamQuestionDTO {
	Long questionId;
	String questionText;
	List<ExamQuestionOptionsDTO> options;
}
