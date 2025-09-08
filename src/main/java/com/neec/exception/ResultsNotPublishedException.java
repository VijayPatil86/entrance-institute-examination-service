package com.neec.exception;

/**
 * Custom exception thrown when a user tries to access their exam result
 * before it has been officially published.
 * This exception is mapped to a 403 Forbidden HTTP status.
 */
public class ResultsNotPublishedException extends RuntimeException {
	public ResultsNotPublishedException(String message) {
		super(message);
	}
}
