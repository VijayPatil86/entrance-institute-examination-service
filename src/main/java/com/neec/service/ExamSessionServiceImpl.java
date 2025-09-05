package com.neec.service;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.ExamSessionDTO;
import com.neec.entity.ExamSession;
import com.neec.enums.ExamStatus;
import com.neec.exception.SessionConflictException;
import com.neec.function.impl.ExamSession_To_ExamSessionDTO_Mapper;
import com.neec.repository.ExamSessionRepository;

@Service
@Transactional
public class ExamSessionServiceImpl implements ExamSessionService {
	final private ExamSessionRepository examSessionRepository;
	final private ExamSession_To_ExamSessionDTO_Mapper examSession_To_ExamSessionDTO_Mapper;

	public ExamSessionServiceImpl(ExamSessionRepository examSessionRepository,
			ExamSession_To_ExamSessionDTO_Mapper examSession_To_ExamSessionDTO_Mapper) {
		this.examSessionRepository = examSessionRepository;
		this.examSession_To_ExamSessionDTO_Mapper = examSession_To_ExamSessionDTO_Mapper;
	}

	@Transactional
	@Override
	public ExamSessionDTO createExamSession(Long userId) {
		Optional<ExamSession> optExistingExamSession =
				examSessionRepository.findByUserIdAndExamStatus(userId, ExamStatus.IN_PROGRESS);
		if(optExistingExamSession.isPresent())
			throw new SessionConflictException("User with ID " + userId + " already has an active exam session.");
		ExamSession newExamSession = ExamSession.builder()
				.userId(userId)
				// The status and startTime are set by default in the entity itself.
				.build();
		ExamSession savedExamSession = examSessionRepository.save(newExamSession);
		return toExamSessionDTO(savedExamSession);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<ExamSessionDTO> findActiveSessionByUserId(Long userId) {
		return examSessionRepository.findByUserIdAndExamStatus(userId, ExamStatus.IN_PROGRESS)
				.map(examSession_To_ExamSessionDTO_Mapper);
	}

	private ExamSessionDTO toExamSessionDTO(ExamSession examSession) {
		return ExamSessionDTO.builder()
				.sessionId(examSession.getSessionId())
				.userId(examSession.getUserId())
				.startTime(examSession.getStartTime())
				.endTime(examSession.getEndTime())
				.examStatus(examSession.getExamStatus())
				.build();
	}
}
