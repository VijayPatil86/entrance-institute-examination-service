package com.neec.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.ExamQuestionDTO;
import com.neec.dto.ExamResultDTO;
import com.neec.dto.SubmitAnswerRequestDTO;
import com.neec.entity.ExamResult;
import com.neec.entity.ExamSession;
import com.neec.entity.Question;
import com.neec.entity.QuestionOption;
import com.neec.entity.SessionQuestion;
import com.neec.enums.ExamStatus;
import com.neec.exception.ResultsNotPublishedException;
import com.neec.function.impl.ExamResult_To_ExamResultDTO_Mapper;
import com.neec.function.impl.Question_Options_To_ExamQuestionDTO;
import com.neec.repository.ExamResultRepository;
import com.neec.repository.ExamSessionRepository;
import com.neec.repository.QuestionOptionRepository;
import com.neec.repository.QuestionRepository;
import com.neec.repository.SessionQuestionRepository;

@Transactional
@Service
public class ExaminationServiceImpl implements ExaminationService {
	final private ExamSessionService examSessionService;
	final private ExamSessionRepository examSessionRepository;
	final private ExamResultRepository examResultRepository;
	final private StudentAnswerService studentAnswerService;
	final private QuestionRepository questionRepository;
	final private QuestionOptionRepository questionOptionRepository;
	final private SessionQuestionRepository sessionQuestionRepository;
	final private Question_Options_To_ExamQuestionDTO question_Options_To_ExamQuestionDTO;
	final private ExamResult_To_ExamResultDTO_Mapper examResult_To_ExamResultDTO_Mapper;

	@Value("${exam.question.count:5}")
	private int examQuestionCount;

	public ExaminationServiceImpl(ExamSessionService examSessionService,
			StudentAnswerService studentAnswerService,
			ExamSessionRepository examSessionRepository,
			ExamResultRepository examResultRepository,
			QuestionRepository questionRepository,
			QuestionOptionRepository questionOptionRepository,
			SessionQuestionRepository sessionQuestionRepository, 
			Question_Options_To_ExamQuestionDTO question_Options_To_ExamQuestionDTO,
			ExamResult_To_ExamResultDTO_Mapper examResult_To_ExamResultDTO_Mapper) {
		this.examSessionService = examSessionService;
		this.studentAnswerService = studentAnswerService;
		this.examSessionRepository = examSessionRepository;
		this.examResultRepository = examResultRepository;
		this.questionRepository = questionRepository;
		this.questionOptionRepository = questionOptionRepository;
		this.sessionQuestionRepository = sessionQuestionRepository;
		this.question_Options_To_ExamQuestionDTO = question_Options_To_ExamQuestionDTO;
		this.examResult_To_ExamResultDTO_Mapper = examResult_To_ExamResultDTO_Mapper;
	}

	@Override
	public ExamQuestionDTO startExam(Long userId) {
		// 1. Create a new exam session (throws SessionConflictException if one is active)
		ExamSession examSession = examSessionService.createExamSession(userId);
		// 2. Fetch all available question IDs from the question bank
		List<Long> allQuestionIds = questionRepository.findAllQuestionIds();
		// 3. Validate that the question bank is not empty
		if(allQuestionIds.isEmpty()) {
			throw new IllegalStateException("Cannot start exam: The question bank is empty.");
		}
		if(allQuestionIds.size() < examQuestionCount) {
			throw new IllegalStateException("Cannot start exam: Not enough questions "
					+ "in the bank to meet the required count of " + 
					examQuestionCount);
		}
		// 4. Randomize and select the final set of questions for this exam
		Collections.shuffle(allQuestionIds);
		List<Long> selectedQuestionIds = allQuestionIds.stream()
				.limit(examQuestionCount)
				.toList();
		// 5. Persist the student's unique "question playlist"
		List<SessionQuestion> playList = IntStream.range(0, selectedQuestionIds.size())
				.mapToObj(index -> {
					Question question = questionRepository.getReferenceById(selectedQuestionIds.get(index));
					return SessionQuestion.builder()
						.examSession(examSession)
						.question(question)
						.questionSequenceNumber(index)
						.build();
				})
				.toList();
		sessionQuestionRepository.saveAll(playList);
		// 6. Fetch the full details of the VERY FIRST question to return
		Long firstQuestionId = selectedQuestionIds.get(0);
		Question firstQuestion = questionRepository.findById(firstQuestionId)
				.orElseThrow(() -> new IllegalStateException(
						"Data integrity issue: Could not find first question with ID: " +
				firstQuestionId));
		List<QuestionOption> firstQuestionOptions = 
				questionOptionRepository.findByQuestion_QuestionId(firstQuestionId);
		// 7. Map to a "safe" DTO and return
		return question_Options_To_ExamQuestionDTO.apply(firstQuestion, firstQuestionOptions);
	}

	@Override
	public Optional<ExamQuestionDTO> submitAnswerAndGetNext(Long userId, SubmitAnswerRequestDTO answerDTO) {
		// 1. Delegate to the specialist service to save the answer.
		// This call contains all necessary validation (active session, question exists, etc.).
		studentAnswerService.saveAnswer(userId, answerDTO);
		// 2. Fetch the user's session again, this time with the full question playlist.
		ExamSession examSession = 
				examSessionRepository.findByUserIdAndExamStatusWithQuestionsPlaylist(userId, ExamStatus.IN_PROGRESS)
				.orElseThrow(() -> new IllegalStateException("Could not find active session after saving answer."));
		// 3. Find the sequence number of the question that was just answered.
		int currentQuestionSeqNumber = examSession.getQuestionPlayList().stream()
				.filter(question -> question.getQuestion().getQuestionId().equals(answerDTO.getQuestionId()))
				.findFirst()
				.map(question -> question.getQuestionSequenceNumber())
				.orElseThrow(() -> new IllegalStateException("Answered question is not part of this exam session."));
		// 4. Determine the next sequence number.
		int nextQuestionSeqNumber = currentQuestionSeqNumber + 1;
		// 5. Check if the exam is over.
		if(nextQuestionSeqNumber >= examSession.getQuestionPlayList().size()) {
			// Exam is finished. Mark the session as completed.
			examSessionService.completeSession(examSession.getSessionId(), userId);
			// Return an empty Optional to signal the end of the exam.
			return Optional.empty();
		}
		// 6. If the exam is not over, get the next question from the playlist.
		SessionQuestion nextSessionQuestion = examSession.getQuestionPlayList().get(nextQuestionSeqNumber);
		Question nextQuestion = nextSessionQuestion.getQuestion();
		List<QuestionOption> nextQuestionOptions =
				questionOptionRepository.findByQuestion_QuestionId(nextQuestion.getQuestionId());
		return Optional.of(
					question_Options_To_ExamQuestionDTO.apply(nextQuestion, nextQuestionOptions)
				);
	}

	@Transactional(readOnly = true)
	@Override
	public ExamResultDTO getMyResult(Long userId) {
		// 1. Find the user's most recent exam result.
		ExamResult examResult = examResultRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
				.orElseThrow(() -> new NoSuchElementException("No exam result found for this user."));
		// 2. CRITICAL: Check if the results have been officially published.
		if(examResult.getResultPublishDate() == null ||
				examResult.getResultPublishDate().isAfter(Instant.now()))
			throw new ResultsNotPublishedException("The results for this exam have not been published yet.");
		// 3. If checks pass, map the entity to a DTO and return it.
		return examResult_To_ExamResultDTO_Mapper.apply(examResult);
	}

}
