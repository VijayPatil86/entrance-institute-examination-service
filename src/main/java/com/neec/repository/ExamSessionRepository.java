package com.neec.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamSession;
import com.neec.enums.ExamStatus;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
	/**
     * Finds an exam session for a given user that is currently in a specific status.
     * This is the key method to check if a user already has an active exam.
     *
     * @param userId The ID of the user.
     * @param status The status to check for (e.g., IN_PROGRESS).
     * @return An Optional containing the ExamSession if found.
     */
	Optional<ExamSession> findByUserIdAndExamStatus(Long userId, ExamStatus examStatus);
}
