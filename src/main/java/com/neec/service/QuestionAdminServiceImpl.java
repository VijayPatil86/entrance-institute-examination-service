package com.neec.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional(readOnly = true)
	public QuestionResponseDTO getQuestionById(Long questionId) {
		Question question = questionRepository.findByQuestionIdWithOptions(questionId)
				.orElseThrow(() -> new NoSuchElementException("Question with id " + questionId + " not found"));
		List<QuestionOption> listQuestionOptions =
				questionOptionRepository.findByQuestion_QuestionId(questionId);
		if(listQuestionOptions.isEmpty()) {
			throw new IllegalStateException("Question with id " + questionId + " has no associated options");
		}
		List<QuestionOptionsResponseDTO> listQuestionOptionsResponseDTOs =
				listQuestionOptions.stream()
					.map(option ->
						QuestionOptionsResponseDTO.builder().optionId(option.getOptionId())
							.optionLabel(option.getOptionLabel())
							.optionText(option.getOptionText())
							.build())
					.toList();
		QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.questionText(question.getQuestionText())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.correctOptionLabel(question.getCorrectOption().getOptionLabel())
				.options(listQuestionOptionsResponseDTOs)
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.build();
		return questionResponseDTO;
	}

	public List<QuestionResponseDTO> getAllQuestions(Pageable pageable) {
		Page<Question> pageQuestion = questionRepository.findAll(pageable);
		List<QuestionResponseDTO> page = pageQuestion.map(question ->
			QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.questionText(question.getQuestionText())
				.correctOptionLabel(question.getCorrectOption().getOptionLabel())
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.options(
						questionOptionRepository.findByQuestion_QuestionId(question.getQuestionId()).stream()
							.map(option ->
								QuestionOptionsResponseDTO.builder()
									.optionId(option.getOptionId())
									.optionLabel(option.getOptionLabel())
									.optionText(option.getOptionText())
									.build())
							.toList())
				.build()
		).toList();
		return page;
	}

	public List<QuestionResponseDTO> findBySubject(String subject, Pageable pageable) {
		Page<Question> pageQuestion = questionRepository.findBySubject(subject, pageable);
		List<QuestionResponseDTO> page = pageQuestion.map(question ->
			QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.questionText(question.getQuestionText())
				.correctOptionLabel(question.getCorrectOption().getOptionLabel())
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.options(
						questionOptionRepository.findByQuestion_QuestionId(question.getQuestionId()).stream()
							.map(option ->
								QuestionOptionsResponseDTO.builder()
									.optionId(option.getOptionId())
									.optionLabel(option.getOptionLabel())
									.optionText(option.getOptionText())
									.build())
							.toList())
				.build()
		).toList();
		return page;
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
