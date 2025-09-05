package com.neec.dto;

import java.time.Instant;

import com.neec.enums.ExamStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder @NoArgsConstructor @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE) @Getter @Setter
public class ExamSessionDTO {
	Long sessionId;
	Long userId;
	Instant startTime;
	Instant endTime;
	ExamStatus examStatus;
}
