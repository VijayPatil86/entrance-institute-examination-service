package com.neec.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.neec.config.RabbitMQConfig;
import com.neec.service.ResultCalculationService;

/**
 * A message listener component that consumes messages from the result calculation queue.
 * This class is the bridge between the RabbitMQ messaging system and our business logic.
 */
@Component
public class ResultCalculationListener {
	private static final Logger log = LoggerFactory.getLogger(ResultCalculationListener.class);
	final private ResultCalculationService resultCalculationService;

	public ResultCalculationListener(ResultCalculationService resultCalculationService) {
		this.resultCalculationService = resultCalculationService;
	}

	/**
     * Handles incoming messages containing the ID of a completed exam session.
     * The @RabbitListener annotation automatically subscribes this method to the specified queue.
     *
     * @param sessionIdMessage The message payload, expected to be a String representation of the session ID.
     */
	@RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME})
	public void handleResultCalculation(String sessionIdMessage) {
		log.info("Received message to calculate result for session: {}", sessionIdMessage);
		try {
			Long sessionId = Long.parseLong(sessionIdMessage);
			// Delegate the entire business logic to the specialist service.
			resultCalculationService.calculateAndSaveResult(sessionId);
			log.info("Successfully calculated and saved result for session: {}", sessionId);
		} catch(NumberFormatException ex) {
			// This handles cases where the message is malformed.
			log.error("Failed to parse session ID from message: '{}'. The message will be discarded.",
					sessionIdMessage, ex);
		} catch(Exception ex) {
			// This is a critical catch-all to prevent unhandled exceptions.
            // If an unhandled exception is thrown, RabbitMQ may requeue the message,
            // leading to an infinite loop of failures.
			log.error("An unexpected error occurred while processing session from message: '{}'. "
					+ "The message will be discarded.", 
					sessionIdMessage, ex);
			// In a production system, you would typically move this "poison pill" message
            // to a dead-letter queue for manual inspection.
		}
	}
}
