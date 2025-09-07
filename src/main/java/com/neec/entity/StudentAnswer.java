package com.neec.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@Entity
@Table(name = "STUDENT_ANSWERS",
		uniqueConstraints = @UniqueConstraint(columnNames = {"QUESTION_ID", "SESSION_ID"})
)
public class StudentAnswer {
	@Column(name = "ANSWER_ID", insertable = false, updatable = false, nullable = false, unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long answerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", insertable = true, updatable = false, nullable = false)
	ExamSession examSession;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUESTION_ID", referencedColumnName = "QUESTION_ID", insertable = true, updatable = false, nullable = false)
	Question question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELECTED_OPTION_ID", referencedColumnName = "OPTION_ID", insertable = true, updatable = false, nullable = false)
	QuestionOption questionOption;

	@Column(name = "IS_ANSWER_CORRECT", insertable = true, updatable = true, unique = false, nullable = true)
	Boolean isAnswerCorrect;

	@Column(name = "SUBMITTED_AT", insertable = true, updatable = false, nullable = false, unique = false)
	@CreationTimestamp
	Instant questionSubmittedAt;
}
