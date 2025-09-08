package com.neec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
@Entity
@Table(
		name = "SESSION_QUESTIONS",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_SESSION_ID_QUESTION_ID",
						columnNames = {"SESSION_ID", "QUESTION_ID"}
				),
				@UniqueConstraint(
						name = "unique_SESSION_ID_SEQUENCE_NUMBER",
						columnNames = {"SESSION_ID", "SEQUENCE_NUMBER"}
				)
		}
)
public class SessionQuestion {
	@Column(name = "SESSION_QUESTION_ID", insertable = false, updatable = false, 
			nullable = false, unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long sessionQuestionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", 
			insertable = true, updatable = true, nullable = false)
	ExamSession examSession;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUESTION_ID", referencedColumnName = "QUESTION_ID", 
			insertable = true, updatable = true, nullable = false)
	Question question;

	@Column(name = "SEQUENCE_NUMBER", insertable = true, updatable = true, 
			nullable = false)
	int questionSequenceNumber;
}

