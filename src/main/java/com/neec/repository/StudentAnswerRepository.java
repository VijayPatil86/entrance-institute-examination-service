package com.neec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.ExamSession;
import com.neec.entity.Question;
import com.neec.entity.StudentAnswer;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
	/**
     * Efficiently checks if an answer already exists for a given session and question.
     * @param examSession The student's current exam session.
     * @param question The question being answered.
     * @return true if an answer already exists, false otherwise.
     */
	boolean existsByExamSessionAndQuestion(ExamSession examSession, Question question);

	List<StudentAnswer> findByExamSession_SessionId(Long sessionId);
}
