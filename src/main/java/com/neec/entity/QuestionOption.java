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
import jakarta.persistence.ManyToOne;
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
@Table(name = "QUESTION_OPTIONS")
public class QuestionOption {
	@Column(name = "OPTION_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long optionId;

	@Column(name = "OPTION_LABEL")
	String optionLabel;	// "A", "B", "C", "D"

	@Column(name = "OPTION_TEXT")
	String optionText;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUESTION_ID", referencedColumnName = "QUESTION_ID", insertable = true, updatable = true, nullable = false, unique = false)
	Question question;

	@Column(name = "CREATED_AT")
	@CreationTimestamp
	Instant createdAt;

	@Column(name = "UPDATED_AT")
	@UpdateTimestamp
	Instant updatedAt;
}
