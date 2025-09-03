package com.neec.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.neec.dto.QuestionRequestDTO;
import com.neec.dto.QuestionResponseDTO;

public interface QuestionAdminService {
	/**
	 * Creates a new question along with its multiple-choice options.
	 * @param questionDTO The DTO containing the new question and its options.
	 * @return A DTO representing the newly created question.
	 */
	QuestionResponseDTO createQuestion(QuestionRequestDTO questionDTO);

	/**
     * Retrieves a single question by its ID.
     * @param questionId The ID of the question to retrieve.
     * @return A DTO representing the question.
     */
    QuestionResponseDTO getQuestionById(Long questionId);

    /**
     * Retrieves a paginated list of all questions in the question bank.
     * @param pageable An object containing pagination information (page number, size).
     * @return A List of DTOs representing the questions for the requested page.
     */
    List<QuestionResponseDTO> getAllQuestions(Pageable pageable);

    /**
     * Retrieves a paginated list of all questions in the question bank for given subject.
     * @param pageable An object containing pagination information (page number, size).
     * @return A List of DTOs representing the questions for the requested page.
     */
    List<QuestionResponseDTO> findBySubject(String subject, Pageable pageable);

    /**
     * Updates an existing question and its options.
     * @param questionId The ID of the question to update.
     * @param questionDTO The DTO containing the updated data.
     * @return A DTO representing the updated question.
     */
    //QuestionResponseDTO updateQuestion(Long questionId, QuestionRequestDTO questionDTO);

    /**
     * Deletes a question from the question bank.
     * @param questionId The ID of the question to delete.
     */
    //void deleteQuestion(Long questionId);

	/**
     * Restores a question from the question bank.
     * @param questionId The ID of the question to restore.
     */
    //void restoreQuestion(Long questionId);
}
