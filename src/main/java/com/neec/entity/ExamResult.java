package com.neec.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "EXAM_RESULTS")
public class ExamResult {
	@Column(name = "RESULT_ID", insertable = false, updatable = false, nullable = false, unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long resultId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", insertable = true, 
		updatable = true, unique = true, nullable = false)
	ExamSession examSession;

	@Column(name = "USER_ID", insertable = true, updatable = true, nullable = false, unique = true)
	Long userId;

	@Column(name = "SCORE", insertable = true, updatable = true, nullable = false, unique = false)
	Integer score;

	@Column(name = "TOTAL_QUESTIONS", insertable = true, updatable = true, nullable = false, unique = false)
	Integer totalQuestions;

	@Column(name = "CORRECT_ANSWERS", insertable = true, updatable = true, unique = false, nullable = false)
	Integer correctAnswers;

	@Column(name = "INCORRECT_ANSWERS", insertable = true, updatable = true, nullable = false, unique = false)
	Integer incorrectAnswers;

	// nullable - true: as it may be calculated in a separate step after initial grading
	@Column(name = "EXAM_RANK", insertable = true, updatable = true, unique = false, nullable = true)
	Integer rank;

	// nullable - true: as results may be held until an official announcement date
	@Column(name = "RESULT_PUBLISH_DATE", insertable = true, updatable = true, unique = false, nullable = true)
	Instant resultPublishDate;

	@Column(name = "CREATED_AT", insertable = true, updatable = false, nullable = false, unique = false)
	@CreationTimestamp
	Instant createdAt;

	@Column(name = "UPDATED_AT", insertable = true, updatable = true, nullable = false, unique = false)
	@UpdateTimestamp
	Instant updatedAt;
}
