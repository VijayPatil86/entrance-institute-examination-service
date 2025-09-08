package com.neec.function.impl;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.neec.dto.ExamResultDTO;
import com.neec.entity.ExamResult;

@Component
public class ExamResult_To_ExamResultDTO_Mapper implements Function<ExamResult, ExamResultDTO> {
	@Override
	public ExamResultDTO apply(ExamResult examResult) {
		return ExamResultDTO.builder()
			.userId(examResult.getUserId())
			.score(examResult.getScore())
			.totalQuestions(examResult.getTotalQuestions())
			.correctAnswers(examResult.getCorrectAnswers())
			.incorrectAnswers(examResult.getIncorrectAnswers())
			.rank(examResult.getRank())
			.resultPublishDate(examResult.getResultPublishDate())
			.build();
	}

}
