package com.neec.service;

import java.util.Optional;

import com.neec.dto.ExamQuestionDTO;
import com.neec.dto.ExamResultDTO;
import com.neec.dto.SubmitAnswerRequestDTO;

/**
	this interface is an orchestrator interface
	Service interface for orchestrating the student-facing examination flow.
 	This service acts as coordinator, co-ordinating with other specialist services
to manage the entire process from starting the exam to getting the result.
	Orchestrator -
		an orchestrator in software architecture is a component that coordinates multiple other services or steps, 
managing the sequence and flow of logic.
		In Spring Boot, this often happens in the Service layer, and we can define an interface 
to represent this orchestration contract.
	Controller(Delegates to orchestrator interface) --> 
	Orchestrator interface(Declares orchestration API) --> Impl class(Coordinates repositories & business logic)
 */
public interface ExaminationService {

    /**
     * Starts the exam for a given user.
     * This orchestrates creating an exam session, fetching a randomized set of questions,
     * and returning the first question.
     *
     * @param userId The ID of the user starting the exam.
     * @return The first question of the exam.
     */
    ExamQuestionDTO startExam(Long userId);

    /**
     * Submits a student's answer and retrieves the next question in the exam.
     * This orchestrates saving the answer and then determining the next question to serve.
     * If there are no more questions, the exam session is marked as completed.
     *
     * @param userId      The ID of the user submitting the answer.
     * @param answerDTO   The DTO containing the answer details.
     * @return An Optional containing the next question. If the Optional is empty, the exam is finished.
     */
    Optional<ExamQuestionDTO> submitAnswerAndGetNext(Long userId, SubmitAnswerRequestDTO answerDTO);

    /**
     * Retrieves the final exam result for the authenticated user.
     * This method will have logic to ensure results are only visible after they have been
     * officially published by an administrator.
     *
     * @param userId The ID of the user requesting their result.
     * @return A DTO containing the final score and rank.
     */
    ExamResultDTO getMyResult(Long userId);
}
