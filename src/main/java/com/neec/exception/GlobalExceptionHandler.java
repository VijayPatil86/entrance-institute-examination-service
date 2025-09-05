package com.neec.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private final ObjectMapper objectMapper;

	public GlobalExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@ExceptionHandler(exception = {MethodArgumentNotValidException.class})
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		// class level errors like @ValidExamSlotTime
		ex.getGlobalErrors().forEach(error ->
			errors.put(error.getObjectName(), error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(exception = {NoSuchElementException.class})
	public ResponseEntity<ObjectNode> handleNoSuchElementException(NoSuchElementException ex){
		ObjectNode errorMessage = objectMapper.createObjectNode();
		errorMessage.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
	}

	@ExceptionHandler(exception = {IllegalStateException.class})
	public ResponseEntity<ObjectNode> handleIllegalStateException(IllegalStateException ex){
		ObjectNode errorMessage = objectMapper.createObjectNode();
		errorMessage.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
	}

	@ExceptionHandler(exception = {Exception.class})
	public ResponseEntity<ObjectNode> handleException(Exception ex) {
		ObjectNode errorMessage = objectMapper.createObjectNode();
		errorMessage.put("error", "Unexpected error: " + ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
	}

	@ExceptionHandler(exception = {SessionConflictException.class})
	public ResponseEntity<ObjectNode> handleSessionConflictException(SessionConflictException ex) {
		ObjectNode errorMessage = objectMapper.createObjectNode();
		errorMessage.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
	}

	@ExceptionHandler(exception = {IllegalArgumentException.class})
	public ResponseEntity<ObjectNode> handleIllegalArgumentException(IllegalArgumentException ex) {
		ObjectNode errorMessage = objectMapper.createObjectNode();
		errorMessage.put("error", ex.getMessage());
		return ResponseEntity.badRequest().body(errorMessage);
	}
}
