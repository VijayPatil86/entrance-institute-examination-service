package com.neec.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.CustomPrincipal;
import com.neec.dto.ExamSessionDTO;
import com.neec.service.ExamSessionService;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/sessions")
public class ExamSessionController {
	final private ExamSessionService examSessionService;

	public ExamSessionController(ExamSessionService examSessionService) {
		this.examSessionService = examSessionService;
	}

	/**
     * Creates a new exam session for the authenticated user.
     * @param customPrincipal The authenticated user's details, injected by Spring Security.
     * @return A 201 Created response.
     */
	@PostMapping
	public ResponseEntity<ExamSessionDTO> createExamSession(@AuthenticationPrincipal CustomPrincipal customPrincipal){
		Long userId;
		try {
			userId = Long.valueOf(customPrincipal.getSubject());
		} catch (NumberFormatException e) {
			// This would result in a 400 Bad Request if the JWT subject is not a valid number.
			throw new IllegalArgumentException("Invalid user ID format in JWT subject.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(examSessionService.createExamSession(userId));
	}

	@GetMapping("/me/active")
	public ResponseEntity<?> getActiveSession(@AuthenticationPrincipal CustomPrincipal customPrincipal){
		Long userId;
		try {
			userId = Long.valueOf(customPrincipal.getSubject());
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid user ID format in JWT subject.");
		}
		Optional<ExamSessionDTO> optActiveSession =
				examSessionService.findActiveSessionByUserId(userId);
		if(optActiveSession.isPresent()) {
			return ResponseEntity.ok(optActiveSession.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/{sessionId}/complete")
	public ResponseEntity<ExamSessionDTO> completeSession(
			@PathVariable(name = "sessionId") Long sessionId,
			@AuthenticationPrincipal CustomPrincipal customPrincipal){
		Long userId;
		try {
			userId = Long.parseLong(customPrincipal.getSubject());
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid user ID format in JWT subject.");
		}
		return ResponseEntity.ok(examSessionService.completeSession(sessionId, userId));
	}
}
