package com.neec.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.QuestionRequestDTO;
import com.neec.dto.QuestionResponseDTO;
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

	@GetMapping("/questions/{questionId}")
	public ResponseEntity<?> getQuestionById(@PathVariable(name = "questionId") Long questionId){
		return ResponseEntity.ok(questionAdminService.getQuestionById(questionId));
	}

	/**
     * Retrieves a paginated and sorted list of all questions.
     * @param pageable An object containing pagination and sorting information, automatically populated from URL parameters
     * like ?page=0&size=20&sort=updatedAt,desc.
     * @return A 200 OK response with a Page object containing the questions and pagination metadata.
     */
	/*
	 getAllQuestions(
		@PageableDefault(size = 10)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "field1", direction = Sort.Direction.ASC),
            @SortDefault(sort = "field2", direction = Sort.Direction.DESC)
        }) Pageable pageable)
        defaults: page = 0, size = 20
	 */
	// http://localhost:9055/api/v1/admin/questions?page=0&size=10&sort=subject,desc&sort=questionDifficultyLevel,asc
	@GetMapping("/questions")
	public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions(
		@PageableDefault(page = 0, size = 5, direction = Sort.Direction.DESC, sort = "updatedAt") Pageable pageable ) {
		return ResponseEntity.ok(questionAdminService.getAllQuestions(pageable));
	}

	// http://localhost:9055/api/v1/admin/questions?page=0&size=7&sort=questionDifficultyLevel&subject=English
	@GetMapping(value = "/questions", params = {"subject"})
	public ResponseEntity<List<QuestionResponseDTO>> getAllQuestionsForSubject(
		@RequestParam(name = "subject") String subject,
		@PageableDefault(page = 0, size = 5, direction = Sort.Direction.DESC, sort = "updatedAt") Pageable pageable ) {
		return ResponseEntity.ok(questionAdminService.findBySubject(subject, pageable));
	}
}
