package com.neec.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.Question;
import com.neec.entity.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
	List<QuestionOption> findByQuestion_QuestionId(Long quetionId);
	List<QuestionOption> findByQuestionIn(List<Question> questions);
	/**
     * Finds a QuestionOption by its ID and eagerly fetches the associated Question
     * entity in a single query to optimize performance.
     *
     * @param optionId The ID of the option to find.
     * @return An Optional containing the QuestionOption with its Question initialized.
     */
	@EntityGraph(attributePaths = {"question"})
	Optional<QuestionOption> findByOptionId(Long optionId);
}
