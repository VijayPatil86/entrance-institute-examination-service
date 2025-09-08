package com.neec.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamResult;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
	/**
     * Checks if an ExamResult exists for a given session ID.
     * This is used to prevent duplicate result calculations.
     * @param sessionId The ID of the exam session.
     * @return true if a result exists, false otherwise.
     */
	boolean existByExamSession_SessionId(Long sessionId);

	/**
     * Finds the most recent exam result for a given user.
     * @param userId The ID of the user.
     * @return An Optional containing the user's exam result.
     */
	Optional<ExamResult> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
