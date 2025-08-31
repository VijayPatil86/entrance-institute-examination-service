package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
