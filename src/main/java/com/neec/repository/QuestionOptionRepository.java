package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

}
