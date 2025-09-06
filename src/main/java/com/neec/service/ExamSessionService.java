package com.neec.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.neec.dto.ExamSessionDTO;
import com.neec.enums.ExamStatus;

/**
 * Service interface for managing the lifecycle of an ExamSession.
 * This service handles the creation, retrieval, and completion of exam sessions,
 * providing the foundational logic for the main ExaminationService.
 */
public interface ExamSessionService {
	/**
     * Creates a new exam session for a given user.
     * This method should prevent a user from starting a new session if they already have one in progress.
     *
     * @param userId The ID of the user starting the exam.
     * @return The newly created ExamSession DTO.
     * @throws com.neec.exception.SessionConflictException if the user already has an active session (results in HTTP 409 Conflict).
     */
	ExamSessionDTO createExamSession(Long userId);

	/**
     * Retrieves the active (IN_PROGRESS) exam session for a specific user.
     * This is crucial for validating that a user is in an active exam before they can submit answers.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the active ExamSession if one exists, otherwise an empty Optional.
     */
    Optional<ExamSessionDTO> findActiveSessionByUserId(Long userId);

    /**
     * Marks an exam session as completed.
     * This involves setting the session's status to COMPLETED and recording the end time.
     *
     * @param sessionId The ID of the session to complete.
     * @param userId The ID of the user.
     */
    ExamSessionDTO completeSession(Long sessionId, Long userId);

    /**
     * (For Admin) Retrieves a single exam session by its ID, regardless of its status.
     *
     * @param sessionId The ID of the session.
     * @return An Optional containing the active ExamSession if one exists, otherwise an empty Optional.
     */
    Optional<ExamSessionDTO> getSessionById(Long sessionId);

    /**
     * (For Admin) Retrieves a paginated list of all exam sessions, optionally filtered by status.
     *
     * @param status   An optional status to filter by (e.g., IN_PROGRESS, COMPLETED).
     * @param pageable Pagination and sorting information.
     * @return A Page of ExamSession entities.
     */
    Page<ExamSessionDTO> getAllSessions(Optional<ExamStatus> status, Pageable pageable);
}
