package com.neec.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.QuestionOptionsRequestDTO;
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
				.options(buildAndReturnQuestionResponseDTO(savedOptions))
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

	@Transactional(readOnly = true)
	public Page<QuestionResponseDTO> getAllQuestions(Pageable pageable) {
		Page<Question> pageQuestion = questionRepository.findAll(pageable);
		List<Question> questionsOnPage = pageQuestion.getContent();
		if(questionsOnPage.isEmpty()) {
			return Page.empty();
		}
		List<QuestionOption> allOptionsOnPage = questionOptionRepository.findByQuestionIn(questionsOnPage);
		Map<Long, List<QuestionOption>> mapQuestionOptions = allOptionsOnPage.stream()
				.collect(Collectors.groupingBy(option -> option.getQuestion().getQuestionId()));
		Page<QuestionResponseDTO> page = pageQuestion.map(question ->
			QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.questionText(question.getQuestionText())
				.correctOptionLabel(question.getCorrectOption().getOptionLabel())
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.options(mapQuestionOptions.get(question.getQuestionId()).stream()
						.map(questionOption ->
							QuestionOptionsResponseDTO.builder()
								.optionId(questionOption.getOptionId())
								.optionLabel(questionOption.getOptionLabel())
								.optionText(questionOption.getOptionText())
								.build())
						.toList())
				.build());
		return page;
	}

	@Transactional(readOnly = true)
	public Page<QuestionResponseDTO> getQuestionsBySubject(String subject, Pageable pageable) {
		Page<Question> pageQuestion = questionRepository.findAllBySubject(subject, pageable);
		List<Question> questionsOnPage = pageQuestion.getContent();
		if(questionsOnPage.isEmpty()) {
			return Page.empty();
		}
		List<QuestionOption> allOptionsOnPage = questionOptionRepository.findByQuestionIn(questionsOnPage);
		Map<Long, List<QuestionOption>> mapQuestionOptions = allOptionsOnPage.stream()
				.collect(Collectors.groupingBy(option -> option.getQuestion().getQuestionId()));
		Page<QuestionResponseDTO> page = pageQuestion.map(question ->
			QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.questionText(question.getQuestionText())
				.correctOptionLabel(question.getCorrectOption().getOptionLabel())
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.options(mapQuestionOptions.get(question.getQuestionId()).stream()
						.map(questionOption ->
							QuestionOptionsResponseDTO.builder()
								.optionId(questionOption.getOptionId())
								.optionLabel(questionOption.getOptionLabel())
								.optionText(questionOption.getOptionText())
								.build())
						.toList())
				.build());
		return page;
	}

	@Transactional
	public QuestionResponseDTO updateQuestion(Long questionId, QuestionRequestDTO incomingQuestionDTO) {
		// 1. Fetch the existing question and its options in an efficient way
		Question question = questionRepository.findByQuestionIdWithOptions(questionId)
				.orElseThrow(() -> new NoSuchElementException("Question with id " + questionId + " not found"));
		List<QuestionOption> existingOptions = questionOptionRepository.findByQuestion_QuestionId(questionId);
		// Create a lookup map of existing options by their label for quick access
		Map<String, QuestionOption> mapExistingOptions = existingOptions.stream()
				.collect(Collectors.toMap(option -> option.getOptionLabel(), Function.identity()));
		List<QuestionOption> finalOptions = new ArrayList<>();
		// 2. Process incoming options: update existing ones and create new ones
		for(QuestionOptionsRequestDTO incomingQuestionOptionsRequestDTO : incomingQuestionDTO.getOptions()) {
			QuestionOption existingOption = mapExistingOptions.get(incomingQuestionOptionsRequestDTO.getOptionLabel());
			if(existingOption != null) {
				// UPDATE: This option already exists, so update its text
				existingOption.setOptionText(incomingQuestionOptionsRequestDTO.getOptionText());
				finalOptions.add(existingOption);
				// Remove from map so we know it's been handled
				mapExistingOptions.remove(incomingQuestionOptionsRequestDTO.getOptionLabel());
			} else {
				// INSERT: This is a new option, create and add it
				QuestionOption newQuestionOption = QuestionOption.builder()
						.optionLabel(incomingQuestionOptionsRequestDTO.getOptionLabel())
						.optionText(incomingQuestionOptionsRequestDTO.getOptionText())
						.question(question)
						.build();
				finalOptions.add(newQuestionOption);
			}
		}
		// 3. DELETE: Any options left in the map were not in the incoming DTO, so delete them
		if(!mapExistingOptions.isEmpty()) {
			questionOptionRepository.deleteAll(mapExistingOptions.values());
		}
		// 4. Save all new + updated options in a single batch operation
		List<QuestionOption> savedOptions = questionOptionRepository.saveAll(finalOptions);
		// 5. Get Correct Option entity
		QuestionOption correctOption = savedOptions.stream()
				.filter(option -> option.getOptionLabel().equals(incomingQuestionDTO.getCorrectOptionLabel()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Correct option label does not match any provided options."));
		question.setSubject(incomingQuestionDTO.getSubject());
		question.setQuestionDifficultyLevel(incomingQuestionDTO.getDifficultyLevel());
		question.setQuestionText(incomingQuestionDTO.getQuestionText());
		question.setCorrectOption(correctOption);
		// No need to call questionRepository.save(question), dirty checking handles it, i.e. saves it
		QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.builder()
				.questionId(question.getQuestionId())
				.subject(question.getSubject())
				.difficultyLevel(question.getQuestionDifficultyLevel())
				.questionText(question.getQuestionText())
				.correctOptionLabel(correctOption.getOptionLabel())
				.options(buildAndReturnQuestionResponseDTO(
						savedOptions.stream().sorted(Comparator.comparing(QuestionOption::getOptionLabel)).toList()))
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.build();
		return questionResponseDTO;
	}

	@Transactional
	public void deleteQuestion(Long questionId) {
		int deletedRowsCount = questionRepository.deactivateById(questionId);
		if(deletedRowsCount == 0) {
			throw new NoSuchElementException("Question with id " + questionId + " not found");
		}
	}

	@Transactional
	public void deleteQuestionsBySubject(String subject) {
		int deletedRowsCount = questionRepository.deactivateBySubject(subject);
		if(deletedRowsCount == 0) {
			throw new NoSuchElementException("Question with subject " + subject + " not found");
		}
	}

	@Transactional
	public QuestionResponseDTO restoreQuestion(Long questionId) {
		int updated = questionRepository.restoreQuestionById(questionId);
		if(updated == 0) {
			throw new NoSuchElementException("Question with id " + questionId + " not found or already active");
		}
		return getQuestionById(questionId);
	}

	@Transactional
	public Page<QuestionResponseDTO> restoreQuestionsBySubject(String subject, Pageable pageable) {
		int updated = questionRepository.restoreQuestionsBySubject(subject);
		if(updated == 0) {
			throw new NoSuchElementException("Question with subject " + subject + " not found or already active");
		}
		return getQuestionsBySubject(subject, pageable);
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

	private List<QuestionOptionsResponseDTO> buildAndReturnQuestionResponseDTO(List<QuestionOption> savedOptions) {
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
