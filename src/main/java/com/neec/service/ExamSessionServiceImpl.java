package com.neec.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neec.dto.ExamSessionDTO;
import com.neec.entity.ExamSession;
import com.neec.enums.ExamStatus;
import com.neec.exception.SessionConflictException;
import com.neec.repository.ExamSessionRepository;

@Service
@Transactional
public class ExamSessionServiceImpl implements ExamSessionService {
	final private ExamSessionRepository examSessionRepository;

	public ExamSessionServiceImpl(ExamSessionRepository examSessionRepository) {
		this.examSessionRepository = examSessionRepository;
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
