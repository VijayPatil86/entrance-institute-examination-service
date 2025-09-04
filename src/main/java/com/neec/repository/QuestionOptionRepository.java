package com.neec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.Question;
import com.neec.entity.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
	List<QuestionOption> findByQuestion_QuestionId(Long quetionId);
	List<QuestionOption> findByQuestionIn(List<Question> questions);
}
