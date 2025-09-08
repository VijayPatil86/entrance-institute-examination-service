package com.neec.function.impl;

import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import com.neec.dto.ExamQuestionDTO;
import com.neec.dto.ExamQuestionOptionsDTO;
import com.neec.entity.Question;
import com.neec.entity.QuestionOption;

@Component
public class Question_Options_To_ExamQuestionDTO implements 
	BiFunction<Question, List<QuestionOption>, ExamQuestionDTO> {

	@Override
	public ExamQuestionDTO apply(Question question, List<QuestionOption> questionOptions) {
		if(question == null)
			return null;
		List<ExamQuestionOptionsDTO> listExamQuestionOptionsDTOs = questionOptions.stream()
			.map(option -> ExamQuestionOptionsDTO.builder()
					.optionId(option.getOptionId())
					.optionLabel(option.getOptionLabel())
					.optionText(option.getOptionText())
					.build())
			.toList();
		return ExamQuestionDTO.builder()
			.questionId(question.getQuestionId())
			.questionText(question.getQuestionText())
			.options(listExamQuestionOptionsDTOs)
			.build();
	}
}
