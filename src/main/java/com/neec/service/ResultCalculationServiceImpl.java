package com.neec.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.entity.ExamResult;
import com.neec.entity.ExamSession;
import com.neec.entity.StudentAnswer;
import com.neec.enums.ExamStatus;
import com.neec.repository.ExamResultRepository;
import com.neec.repository.ExamSessionRepository;
import com.neec.repository.StudentAnswerRepository;

@Service
@Transactional
public class ResultCalculationServiceImpl implements ResultCalculationService {
	final private ExamSessionRepository examSessionRepository;
	final private ExamResultRepository examResultRepository;
	final private StudentAnswerRepository studentAnswerRepository;

	public ResultCalculationServiceImpl(ExamSessionRepository examSessionRepository, 
			ExamResultRepository examResultRepository,
			StudentAnswerRepository studentAnswerRepository) {
		this.examSessionRepository = examSessionRepository;
		this.examResultRepository = examResultRepository;
		this.studentAnswerRepository = studentAnswerRepository;
	}

	@Override
	public ExamResult calculateAndSaveResult(Long sessionId) {
		// 1. Fetch the exam session and validate its state.
		ExamSession examSession = examSessionRepository.findById(sessionId)
				.orElseThrow(() -> new NoSuchElementException("Exam session with ID " + sessionId + " not found."));
		if(!examSession.getExamStatus().equals(ExamStatus.COMPLETED))
			throw new IllegalStateException("Cannot calculate result: Exam session " + sessionId + " is not completed.");
		// 2. Prevent duplicate result calculation.
		if(examResultRepository.existByExamSession_SessionId(examSession.getSessionId()))
			throw new IllegalStateException("A result for exam session " + sessionId + " has already been calculated.");
		// 3. Fetch all answers and the total number of questions for this session.
		List<StudentAnswer> answers = studentAnswerRepository.findByExamSession_SessionId(sessionId);
		int totalQuestions = examSession.getQuestionPlayList().size();
		int correctAnswers = (int) answers.stream()
				.filter(ans -> Objects.equals(true, ans.getIsAnswerCorrect()))
				.count();
		int incorrectAnswers = totalQuestions - correctAnswers;
		// Assuming a simple scoring model: 1 point per correct answer.
		int score = correctAnswers;
		ExamResult examResult = ExamResult.builder()
				.examSession(examSession)
				.userId(examSession.getUserId())
				.score(score)
				.totalQuestions(totalQuestions)
				.correctAnswers(correctAnswers)
				.incorrectAnswers(incorrectAnswers)
				.build();
		// 6. Persist the new result and return it.
		return examResultRepository.save(examResult);
	}

}	
