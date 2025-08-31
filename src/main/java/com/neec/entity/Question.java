package com.neec.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.neec.enums.DifficultyLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@Entity
@Table(name = "QUESTIONS")
@SQLDelete(sql = "update QUESTIONS set IS_ACTIVE = false where QUESTION_ID = ?") // runs when calling the delete() method on a repository.
@SQLRestriction(value = "IS_ACTIVE = true")	// WHERE clause, added in SELECT, UPDATE, and DELETE statements
public class Question {
	@Column(name = "QUESTION_ID", insertable = false, updatable = false, nullable = false, unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long questionId;

	@Column(name = "QUESTION_TEXT", insertable = true, updatable = true, nullable = false, unique = false)
	String questionText;

	@Column(name = "SUBJECT", insertable = true, updatable = true, nullable = false, unique = false)
	String subject;

	@Column(name = "DIFFICULTY_LEVEL", insertable = true, updatable = true, nullable = false, unique = false)
	@Enumerated(EnumType.STRING)
	DifficultyLevel questionDifficultyLevel;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CORRECT_OPTION_ID", referencedColumnName = "OPTION_ID", insertable = true, updatable = true, nullable = true, unique = false)
	QuestionOption correctOption;

	@Column(name = "CREATED_AT", insertable = true, updatable = false, nullable = false, unique = false)
	@CreationTimestamp
	Instant createdAt;

	@Column(name = "UPDATED_AT", insertable = true, updatable = true, nullable = false, unique = false)
	@UpdateTimestamp
	Instant updatedAt;

	@Column(name = "IS_ACTIVE", insertable = false, updatable = true, nullable = false, unique = false)
	boolean isActive;
}
