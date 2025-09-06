package com.neec.service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

	@Transactional(readOnly = true)
	@Override
	public Optional<ExamSessionDTO> getSessionById(Long sessionId){
		return examSessionRepository.findById(sessionId)
			.map(examSession_To_ExamSessionDTO_Mapper);
	}

	@Transactional
	@Override
	public ExamSessionDTO completeSession(Long sessionId, Long userId) {
		ExamSession existingExamSession = examSessionRepository.findById(sessionId)
				.orElseThrow(() -> new NoSuchElementException("ExamSession with ID " + sessionId + " not found."));

		// AUTHORIZATION CHECK
		if(!existingExamSession.getUserId().equals(userId))
			throw new AccessDeniedException("User does not have permission to complete this session.");

		if(existingExamSession.getExamStatus().equals(ExamStatus.COMPLETED))
			throw new SessionConflictException("Exam session is already completed.");

		existingExamSession.setExamStatus(ExamStatus.COMPLETED);
		existingExamSession.setEndTime(Instant.now());
		// The changes will be saved automatically by JPA's dirty checking
	    // when the transactional method completes. No explicit save() is needed.
		return examSession_To_ExamSessionDTO_Mapper.apply(existingExamSession);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ExamSessionDTO> getAllSessions(Optional<ExamStatus> optExamStatus, Pageable pageable) {
		Page<ExamSession> pageExamSession = null;
		if(optExamStatus.isPresent()) {
			// Case 1: A status filter was provided by the client.
			pageExamSession = examSessionRepository.findAllByExamStatus(optExamStatus.get(), pageable);
		} else {
			// Case 2: No filter was provided, so get all sessions.
            // The findAll(Pageable) method is provided by JpaRepository.
			pageExamSession = examSessionRepository.findAll(pageable);
		}
		// Map the resulting Page<ExamSession> to a Page<ExamSessionDTO>.
		// This works correctly and efficiently even if the page is empty, Page.hasContent() = false
		Page<ExamSessionDTO> pageExamSessionDTO = pageExamSession.map(examSession_To_ExamSessionDTO_Mapper);
		return pageExamSessionDTO;
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
