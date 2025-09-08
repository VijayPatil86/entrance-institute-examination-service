package com.neec.service;

import java.util.List;

import com.neec.dto.SubmitAnswerRequestDTO;
import com.neec.dto.SubmitAnswerResponseDTO;
import com.neec.entity.StudentAnswer;

/**
 * Service interface for managing the persistence and retrieval of student answers.
 */
public interface StudentAnswerService {
	/**
     * Saves a student's answer for a specific question during an active exam session.
     * This method contains critical validation logic to ensure the integrity of the exam.
     *
     * @param userId The ID of the currently authenticated student.
     * @param answerDTO The DTO containing the question ID and the selected option ID.
     * @return A DTO representing the successfully saved answer.
     * @throws NoSuchElementException if the active session, question, or selected option does not exist.
     * @throws AccessDeniedException if the selected option does not belong to the given question.
     * @throws AnswerConflictException if the student has already answered this question in this session.
     */
	SubmitAnswerResponseDTO saveAnswer(Long userId, SubmitAnswerRequestDTO dto);
}
