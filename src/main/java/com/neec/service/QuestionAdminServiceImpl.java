package com.neec.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.QuestionOptionsResponseDTO;
import com.neec.dto.QuestionRequestDTO;
import com.neec.dto.QuestionResponseDTO;
import com.neec.entity.Question;
import com.neec.entity.QuestionOption;
import com.neec.repository.QuestionOptionRepository;
import com.neec.repository.QuestionRepository;

@Service
@Transactional
public class QuestionAdminServiceImpl implements QuestionAdminService {
	private QuestionRepository questionRepository;
	private QuestionOptionRepository questionOptionRepository;

	public QuestionAdminServiceImpl(QuestionRepository questionRepository,
			QuestionOptionRepository questionOptionRepository) {
		this.questionRepository = questionRepository;
		this.questionOptionRepository = questionOptionRepository;
	}

	@Transactional
	public QuestionResponseDTO createQuestion(QuestionRequestDTO questionDTO) {
		Question question = Question.builder()
				.subject(questionDTO.getSubject())
				.questionDifficultyLevel(questionDTO.getDifficultyLevel())
				.questionText(questionDTO.getQuestionText())
				.correctOption(null)
				.build();
		Question savedQuestion = questionRepository.save(question);
		List<QuestionOption> options =
				buildAndReturnQuestionsOptionsList(questionDTO, savedQuestion);
		List<QuestionOption> savedOptions =
				questionOptionRepository.saveAll(options);
		QuestionOption correctOption = savedOptions.stream()
				.filter(option -> option.getOptionLabel().equals(questionDTO.getCorrectOptionLabel()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Correct option not found in the provided options list"));
		savedQuestion.setCorrectOption(correctOption); // as this is managed and modified, Hibernate updates it
		QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.builder()
				.questionId(savedQuestion.getQuestionId())
				.subject(savedQuestion.getSubject())
				.difficultyLevel(savedQuestion.getQuestionDifficultyLevel())
				.questionText(savedQuestion.getQuestionText())
				.correctOptionLabel(correctOption.getOptionLabel())
				.options(buildAndReturnQuestionResponseDTO(savedQuestion, savedOptions))
				.createdAt(savedQuestion.getCreatedAt())
				.updatedAt(savedQuestion.getUpdatedAt())
				.build();
		return questionResponseDTO;
	}

	private List<QuestionOption> buildAndReturnQuestionsOptionsList(QuestionRequestDTO questionDTO, Question question) {
		return questionDTO.getOptions().stream()
			.map(option ->
				QuestionOption.builder()
				.optionLabel(option.getOptionLabel())
				.optionText(option.getOptionText())
				.question(question)
				.build()
			)
			.toList();
	}

	private List<QuestionOptionsResponseDTO> buildAndReturnQuestionResponseDTO(Question updatedQuestion,
			List<QuestionOption> savedOptions) {
		return savedOptions.stream()
				.map(savedQuestionOption ->
					QuestionOptionsResponseDTO.builder()
					.optionId(savedQuestionOption.getOptionId())
					.optionLabel(savedQuestionOption.getOptionLabel())
					.optionText(savedQuestionOption.getOptionText())
					.build()
				)
				.toList();
	}
}
