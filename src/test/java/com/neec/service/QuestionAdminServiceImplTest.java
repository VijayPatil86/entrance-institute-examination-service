package com.neec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neec.dto.QuestionOptionsRequestDTO;
import com.neec.dto.QuestionRequestDTO;
import com.neec.dto.QuestionResponseDTO;
import com.neec.entity.Question;
import com.neec.entity.QuestionOption;
import com.neec.enums.DifficultyLevel;
import com.neec.repository.QuestionOptionRepository;
import com.neec.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
public class QuestionAdminServiceImplTest {
	@Mock
	private QuestionRepository mockQuestionRepository;
	@Mock
	private QuestionOptionRepository mockQuestionOptionRepository;
	@InjectMocks
	private QuestionAdminServiceImpl questionAdminServiceImpl;

	@Test
	void test_optionLabelNotFound() {
		Question savedQuestion = Question.builder()
				.questionId(1L)
				.subject("Maths")
				.questionDifficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOption(null)
				.build();
		when(mockQuestionRepository.save(any(Question.class)))
			.thenReturn(savedQuestion);
		List<QuestionOption> savedOptions = List.of(
				QuestionOption.builder().optionId(1L).optionLabel("A").optionText("1").build(),
				QuestionOption.builder().optionId(2L).optionLabel("B").optionText("4").build()
		);
		when(mockQuestionOptionRepository.saveAll(anyList()))
			.thenReturn(savedOptions);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("C")
				.options(List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("1").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("4").build()
				))
				.build();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> questionAdminServiceImpl.createQuestion(questionRequestDTO));
		assertEquals("Correct option not found in the provided options list", ex.getMessage());
		// no need to verify interactions in a sad path test, as the failure is the primary outcome.
		ArgumentCaptor<Question> argCaptorQuestion =
				ArgumentCaptor.forClass(Question.class);
		verify(mockQuestionRepository, times(1)).save(argCaptorQuestion.capture());
		Question toSaveQuestion = argCaptorQuestion.getValue();
		assertEquals("Maths", toSaveQuestion.getSubject());
		assertEquals(DifficultyLevel.EASY, toSaveQuestion.getQuestionDifficultyLevel());
		assertEquals("What is 2+2?", toSaveQuestion.getQuestionText());

		savedQuestion.setQuestionId(1L);
		List<QuestionOption> toSaveOptions = buildAndReturnQuestionsOptionsList(questionRequestDTO, savedQuestion);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<QuestionOption>> argCaptorListQuestionOption =
				(ArgumentCaptor<List<QuestionOption>>) (ArgumentCaptor<?>)
					ArgumentCaptor.forClass(List.class);
		verify(mockQuestionOptionRepository, times(1)).saveAll(argCaptorListQuestionOption.capture());
		List<QuestionOption> captoredListQuestionOption = argCaptorListQuestionOption.getValue();
		assertEquals(toSaveOptions.size(), captoredListQuestionOption.size());
		assertTrue(captoredListQuestionOption.stream().map(option -> option.getOptionLabel()).toList()
				.containsAll(toSaveOptions.stream().map(option -> option.getOptionLabel()).toList()));
		assertEquals("What is 2+2?",
				captoredListQuestionOption.get(0).getQuestion().getQuestionText());
	}

	@Test
	void test_optionLabel_Match() {
		Question savedQuestion = Question.builder()
				.questionId(1L)
				.subject("Maths")
				.questionDifficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOption(null)
				.build();
		when(mockQuestionRepository.save(any(Question.class)))
			.thenReturn(savedQuestion);
		List<QuestionOption> savedOptions = List.of(
				QuestionOption.builder().optionId(1L).optionLabel("A").optionText("1").build(),
				QuestionOption.builder().optionId(2L).optionLabel("B").optionText("4").build()
		);
		when(mockQuestionOptionRepository.saveAll(anyList()))
			.thenReturn(savedOptions);
		QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder()
				.subject("Maths")
				.difficultyLevel(DifficultyLevel.EASY)
				.questionText("What is 2+2?")
				.correctOptionLabel("A")
				.options(List.of(
						QuestionOptionsRequestDTO.builder().optionLabel("A").optionText("1").build(),
						QuestionOptionsRequestDTO.builder().optionLabel("B").optionText("4").build()
				))
				.build();
		QuestionResponseDTO questionResponseDTO = questionAdminServiceImpl.createQuestion(questionRequestDTO);
		ArgumentCaptor<Question> argCaptorQuestion =
				ArgumentCaptor.forClass(Question.class);
		verify(mockQuestionRepository, times(1)).save(argCaptorQuestion.capture());
		Question toSaveQuestion = argCaptorQuestion.getValue();
		assertEquals("Maths", toSaveQuestion.getSubject());
		assertEquals(DifficultyLevel.EASY, toSaveQuestion.getQuestionDifficultyLevel());
		assertEquals("What is 2+2?", toSaveQuestion.getQuestionText());

		savedQuestion.setQuestionId(1L);
		List<QuestionOption> toSaveOptions = buildAndReturnQuestionsOptionsList(questionRequestDTO, savedQuestion);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<QuestionOption>> argCaptorListQuestionOption =
				(ArgumentCaptor<List<QuestionOption>>) (ArgumentCaptor<?>)
					ArgumentCaptor.forClass(List.class);
		verify(mockQuestionOptionRepository, times(1)).saveAll(argCaptorListQuestionOption.capture());
		List<QuestionOption> captoredListQuestionOption = argCaptorListQuestionOption.getValue();
		assertEquals(toSaveOptions.size(), captoredListQuestionOption.size());
		assertTrue(captoredListQuestionOption.stream().map(option -> option.getOptionLabel()).toList()
				.containsAll(toSaveOptions.stream().map(option -> option.getOptionLabel()).toList()));
		assertEquals("What is 2+2?",
				captoredListQuestionOption.get(0).getQuestion().getQuestionText());

		assertEquals(1L, questionResponseDTO.getQuestionId());
		assertEquals("Maths", questionResponseDTO.getSubject());
		assertEquals(DifficultyLevel.EASY, questionResponseDTO.getDifficultyLevel());
		assertEquals("What is 2+2?", questionResponseDTO.getQuestionText());
		assertEquals("A", questionResponseDTO.getCorrectOptionLabel());
		assertEquals(2, questionResponseDTO.getOptions().size());
		assertTrue(questionResponseDTO.getOptions().stream().map(option -> option.getOptionText()).toList()
				.containsAll(List.of("1", "4")));
	}

	private List<QuestionOption> buildAndReturnQuestionsOptionsList(QuestionRequestDTO questionDTO, Question question) {
		return questionDTO.getOptions().stream()
			.map(option ->
				QuestionOption.builder()
				.optionLabel(option.getOptionLabel())
				.optionText(option.getOptionText())
				.question(question)
				.build()
			)
			.toList();
	}
}
