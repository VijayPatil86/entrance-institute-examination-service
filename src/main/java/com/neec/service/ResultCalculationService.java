package com.neec.service;

import com.neec.entity.ExamResult;

/**
 * A specialist service responsible for the asynchronous calculation and saving of exam results.
 * This service is typically triggered by an event or message after an exam session is completed.
 */
public interface ResultCalculationService {
	/**
     * Calculates the final result for a given completed exam session, persists it to the database,
     * and returns the newly created ExamResult entity.
     *
     * This method will:
     * 1. Fetch the exam session and all its associated student answers.
     * 2. Tally the number of correct and incorrect answers.
     * 3. Calculate the final score.
     * 4. Create and save a new ExamResult entity.
     *
     * @param sessionId The ID of the exam session to be graded.
     * @return The newly persisted ExamResult entity.
     * @throws java.util.NoSuchElementException if the exam session with the given ID cannot be found.
     * @throws IllegalStateException if the exam session is not in a 'COMPLETED' state or if a result for this session already exists.
     */
	ExamResult calculateAndSaveResult(Long sessionId);
}
