package com.neec.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.SubmitAnswerRequestDTO;
import com.neec.dto.SubmitAnswerResponseDTO;
import com.neec.entity.ExamSession;
import com.neec.entity.Question;
import com.neec.entity.QuestionOption;
import com.neec.entity.StudentAnswer;
import com.neec.enums.ExamStatus;
import com.neec.exception.AnswerConflictException;
import com.neec.repository.ExamSessionRepository;
import com.neec.repository.QuestionOptionRepository;
import com.neec.repository.QuestionRepository;
import com.neec.repository.StudentAnswerRepository;

@Service
@Transactional
public class StudentAnswerServiceImpl implements StudentAnswerService {
	final private StudentAnswerRepository studentAnswerRepository;
	final private ExamSessionRepository examSessionRepository;
	final private QuestionRepository questionRepository;
	final private QuestionOptionRepository questionOptionRepository;

	public StudentAnswerServiceImpl(StudentAnswerRepository studentAnswerRepository,
			ExamSessionRepository examSessionRepository, 
			QuestionRepository questionRepository, 
			QuestionOptionRepository questionOptionRepository) {
		this.studentAnswerRepository = studentAnswerRepository;
		this.examSessionRepository = examSessionRepository;
		this.questionRepository = questionRepository;
		this.questionOptionRepository = questionOptionRepository;
	}

	@Override
	public SubmitAnswerResponseDTO saveAnswer(Long userId, SubmitAnswerRequestDTO dto) {
		// 1. Find the user's active session
		ExamSession examSession = examSessionRepository.findByUserIdAndExamStatus(userId, ExamStatus.IN_PROGRESS)
				.orElseThrow(() -> new NoSuchElementException("No active exam session found for user ID: " + userId));
		// 2. Find the selected option and its parent question in a single, efficient query
		QuestionOption selectedOption = questionOptionRepository.findByOptionId(dto.getSelectedOptionId())
				.orElseThrow(() -> new NoSuchElementException("Option with ID " + dto.getSelectedOptionId() + " not found."));
		Question question = selectedOption.getQuestion();
		// 3. CRITICAL: Authorization check - Does the option belong to the question from the DTO?\
		// Objects.equals(a, b) - null-safe comparison
		if(!Objects.equals(question.getQuestionId(), dto.getQuestionId()))
			throw new AccessDeniedException("The selected option does not belong to the provided question.");
		// 4. CRITICAL: Business rule check - Has the student already answered this?
		boolean answerExists = studentAnswerRepository.existsByExamSessionAndQuestion(examSession, question);
		if(answerExists)
			throw new AnswerConflictException("You have already submitted an answer for this question.");
		// 5. Determine if the answer is correct
		// don't trust on same data present in DTO
		Boolean isAnswerCorrect = Objects.equals(selectedOption.getOptionId(), question.getCorrectOption().getOptionId());
		StudentAnswer studentAnswer = StudentAnswer.builder()
				.examSession(examSession)
				.question(question)
				.questionOption(selectedOption)
				.isAnswerCorrect(isAnswerCorrect)
				.build();
		StudentAnswer savedStudentAnswer = 
				studentAnswerRepository.save(studentAnswer);
		SubmitAnswerResponseDTO submitAnswerResponseDTO = SubmitAnswerResponseDTO.builder()
				.answerId(savedStudentAnswer.getAnswerId())
				.questionId(question.getQuestionId())
				.selectedOptionId(selectedOption.getOptionId())
				.isCorrectAnswer(isAnswerCorrect)
				.questionSubmittedAt(savedStudentAnswer.getQuestionSubmittedAt())
				.build();
		return submitAnswerResponseDTO;
	}
}
