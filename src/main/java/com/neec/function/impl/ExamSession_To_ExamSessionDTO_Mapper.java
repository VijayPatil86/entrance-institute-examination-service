package com.neec.function.impl;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.neec.dto.ExamSessionDTO;
import com.neec.entity.ExamSession;

@Component
public class ExamSession_To_ExamSessionDTO_Mapper implements Function<ExamSession, ExamSessionDTO> {
	@Override
	public ExamSessionDTO apply(ExamSession examSession) {
		return ExamSessionDTO.builder()
				.sessionId(examSession.getSessionId())
				.userId(examSession.getUserId())
				.startTime(examSession.getStartTime())
				.endTime(examSession.getEndTime())
				.examStatus(examSession.getExamStatus())
				.build();
	}
}
