package com.neec.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.neec.enums.ExamStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "EXAM_SESSIONS")
public class ExamSession {
	@Column(name = "SESSION_ID", insertable = false, updatable = false, nullable = false, unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long sessionId;

	@Column(name = "USER_ID", insertable = true, updatable = false, nullable = false, unique = true)
	Long userId;

	@Column(name = "START_TIME", insertable = true, updatable = false, nullable = false, unique = false)
	@CreationTimestamp
	Instant startTime;

	@Column(name = "END_TIME", insertable = true, updatable = true, nullable = true, unique = false)
	Instant endTime;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	@Builder.Default
	ExamStatus examStatus = ExamStatus.IN_PROGRESS;

	// ADDED: A session now has a list of assigned questions.
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "examSession", cascade = CascadeType.ALL)
	@OrderBy(value = "questionSequenceNumber ASC")
	List<SessionQuestion> questionPlayList;
}
