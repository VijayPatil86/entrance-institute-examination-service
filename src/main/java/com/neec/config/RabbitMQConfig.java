package com.neec.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the necessary RabbitMQ components (exchange, queue, and binding)
 * for handling asynchronous result calculations.
 * RabbitMQ is a separate, independent server process. It is not part of your Spring Boot application.
 1. Message Sent: 
 	A student finishes their exam. Your ExamSessionService sends a message containing the sessionId 
 	to the RabbitMQ server.
 2. Durable Persistence: 
 	We configured our queue (exam.results.queue) to be durable. This means the RabbitMQ server 
 	receives the message and immediately saves it to its own disk. Once this is done, the message is safe, 
 	and RabbitMQ acknowledges this back to your service.
 3. Service Goes Down: 
 	Now, imagine your examination-service microservice crashes for any reason (a bug, server restart, etc.).
 4. Message is Safe: 
 	The message with the sessionId is not lost. It is sitting safely and persistently in the queue 
 	on the RabbitMQ server, completely independent of your application's status.
 5. Service Restarts: 
 	When you restart your examination-service microservice, the @RabbitListener in your ResultCalculationListener 
 	automatically reconnects to the RabbitMQ server.
 6. Message Delivered: 
 	As soon as the listener is ready, RabbitMQ sees that there is a consumer for the exam.results.queue 
 	and immediately delivers the stored message.
 7. Processing Resumes: 
 	Your handleResultCalculation method receives the sessionId and processes it as if nothing ever happened. 
 	The student's result is calculated and saved.
 	
 	In short, using a durable message queue like RabbitMQ guarantees that the task (calculating the result) will 
 	not be forgotten, even if the application that processes it has temporary downtime. This is the fundamental 
 	principle of reliable asynchronous processing.
 */
@Configuration
public class RabbitMQConfig {
	// A best practice to define names as constants to avoid typos.
	public static final String EXCHANGE_NAME = "exam.exchange";
	public static final String QUEUE_NAME = "exam.results.queue";
	public static final String ROUTING_KEY = "exam.session.completed";

	/**
     * Defines the main topic exchange for the examination service.
     * A TopicExchange is flexible and allows for routing messages based on patterns.
     */
	@Bean
	public TopicExchange examExchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}
	
	/**
     * Defines the queue that will hold messages for completed exam sessions
     * waiting to be graded. The queue is durable, meaning messages will be
     * persisted even if the RabbitMQ server restarts.
     */
	@Bean
	public Queue resultCalculationQueue() {
		return new Queue(QUEUE_NAME, true);
	}

	/**
     * Binds the resultCalculationQueue to the examExchange. Any message sent
     * to the exchange with the routing key "exam.session.completed" will be
     * delivered to this queue.
     */
	@Bean
	public Binding binding(Queue resultCalculationQueue, TopicExchange examExchange) {
		return BindingBuilder.bind(resultCalculationQueue).to(examExchange)
				.with(ROUTING_KEY);
	}
}
