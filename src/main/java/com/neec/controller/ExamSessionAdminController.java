package com.neec.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neec.dto.ExamSessionDTO;
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
}
