package com.neec.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.CustomPrincipal;
import com.neec.dto.ExamQuestionDTO;
import com.neec.dto.ExamResultDTO;
import com.neec.dto.SubmitAnswerRequestDTO;
import com.neec.service.ExaminationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/exams")
public class ExaminationController {
	final private ExaminationService examinationService;

	public ExaminationController(ExaminationService examinationService) {
		this.examinationService = examinationService;
	}

	/**
     * Starts the exam for the authenticated student.
     * This creates an exam session and returns the first question.
     * @param customPrincipal The authenticated user's details.
     * @return A 200 OK response with the first question.
     */
	@PostMapping("/start")
	public ResponseEntity<ExamQuestionDTO> startExam(
			@AuthenticationPrincipal CustomPrincipal customPrincipal) {
		Long userId = Long.parseLong(customPrincipal.getSubject());
		ExamQuestionDTO firstQuestion = examinationService.startExam(userId);
		return ResponseEntity.ok(firstQuestion);
	}

	/**
     * Submits an answer for the current question and retrieves the next one.
     * @param customPrincipal The authenticated user's details.
     * @param answerDTO The student's submitted answer.
     * @return A 200 OK response containing the next question, or a 200 OK response
     * with a completion message if the exam is finished.
     */
	@PostMapping("/submit")
	public ResponseEntity<?> submitAnswerAndGetNext(
			@AuthenticationPrincipal CustomPrincipal customPrincipal,
			@Valid @RequestBody SubmitAnswerRequestDTO dto) {
		Long userId = Long.parseLong(customPrincipal.getSubject());
		Optional<ExamQuestionDTO> optNextQuestion =
				examinationService.submitAnswerAndGetNext(userId, dto);
		// // If the optional is empty, the exam is over.
		return optNextQuestion.isPresent() ?
					ResponseEntity.ok(optNextQuestion.get()) :
					ResponseEntity.ok(Map.of("status", "completed",
							"message", "You have successfully completed the exam."));
	}

	/**
     * Retrieves the final exam result for the authenticated student.
     * @param customPrincipal The authenticated user's details.
     * @return A 200 OK with the result if it's published, 403 Forbidden if not yet published,
     * or 404 Not Found if no result exists.
     */
	@GetMapping("/results/me")
	public ResponseEntity<ExamResultDTO> getMyResult(@AuthenticationPrincipal CustomPrincipal customPrincipal) {
		Long userId = Long.parseLong(customPrincipal.getSubject());
		ExamResultDTO examResultDTO = examinationService.getMyResult(userId);
		return ResponseEntity.ok(examResultDTO);
	}
}
