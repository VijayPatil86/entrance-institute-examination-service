package com.neec.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neec.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	@Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.correctOption option WHERE q.questionId = :questionId")
	Optional<Question> findByQuestionIdWithOptions(@Param("questionId") Long questionId);

	@EntityGraph(attributePaths = {"correctOption"})
	Page<Question> findAll(Pageable pageable);

	@EntityGraph(attributePaths = {"correctOption"})
	Page<Question> findBySubject(String subject, Pageable pageable);
}
