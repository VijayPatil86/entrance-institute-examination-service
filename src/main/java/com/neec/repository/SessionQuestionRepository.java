package com.neec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neec.entity.SessionQuestion;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {

}
