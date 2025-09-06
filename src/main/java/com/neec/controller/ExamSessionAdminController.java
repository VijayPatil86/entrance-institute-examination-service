package com.neec.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.ExamSessionDTO;
import com.neec.enums.ExamStatus;
import com.neec.service.ExamSessionService;

@RestController
@CrossOrigin
@RequestMapping(path = {"/api/v1/admin"})
public class ExamSessionAdminController {
	final private ExamSessionService examSessionService;

	public ExamSessionAdminController(ExamSessionService examSessionService) {
		this.examSessionService = examSessionService;
	}

	@GetMapping("/sessions/{sessionId}")
	public ResponseEntity<?> getExamSession(@PathVariable(name = "sessionId") Long sessionId){
		Optional<ExamSessionDTO> optExamSessionDTO =
				examSessionService.getSessionById(sessionId);
		return optExamSessionDTO.isPresent() ?
				ResponseEntity.status(HttpStatus.OK).body(optExamSessionDTO.get()) :
				ResponseEntity.notFound().build();
	}

	@GetMapping(path = {"/sessions"})
	public ResponseEntity<Page<ExamSessionDTO>> getAllExamSessions(
			@RequestParam(name = "status", required = false) ExamStatus status,
			@PageableDefault(page = 0, size = 5, direction = Direction.DESC, sort = {"startTime"}) Pageable pageable){
		// Optional.ofNullable() correctly handles the case where 'status' is null.
		return ResponseEntity.ok(examSessionService.getAllSessions(Optional.ofNullable(status), pageable));
	}
}
