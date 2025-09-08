package com.neec.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Question q SET q.isActive = false WHERE q.questionId = :questionId")
	int deactivateById(@Param("questionId") Long questionId);

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Question q SET q.isActive = false WHERE q.subject = :subject")
	int deactivateBySubject(@Param("subject") String subject);

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Question SET is_active = true WHERE question_id = :questionId", nativeQuery = true)
	int restoreQuestionById(Long questionId);

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Question SET is_active = true WHERE subject = :subject", nativeQuery = true)
	int restoreQuestionsBySubject(@Param("subject") String subject);

	@EntityGraph(attributePaths = {"correctOption"})
	Page<Question> findAllBySubject(String subject, Pageable pageable);

	/**
     * Efficiently retrieves only the IDs of all active questions.
     * This is much faster than loading full entities when we only need the IDs for shuffling.
     * @return A list of all active question IDs.
     */
	@Query(value = "SELECT q.questionId FROM Question q")
	List<Long> findAllQuestionIds();
}
/*
 * @EntityGraph: resolves N+1 problem
 */
