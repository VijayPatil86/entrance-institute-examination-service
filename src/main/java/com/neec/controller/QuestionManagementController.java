package com.neec.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.QuestionRequestDTO;
import com.neec.service.QuestionAdminService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin")
public class QuestionManagementController {
	private QuestionAdminService questionAdminService;

	public QuestionManagementController(QuestionAdminService questionAdminService) {
		this.questionAdminService = questionAdminService;
	}

	@PostMapping("/questions")
	public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
		return ResponseEntity.status(HttpStatus.CREATED.value())
				.body(questionAdminService.createQuestion(questionRequestDTO));
	}
}
