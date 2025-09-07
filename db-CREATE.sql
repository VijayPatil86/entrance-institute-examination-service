create table QUESTIONS (
	QUESTION_ID bigserial primary key,
	QUESTION_TEXT text not null,
	SUBJECT varchar(50) not null,
	DIFFICULTY_LEVEL varchar(30) not null check(DIFFICULTY_LEVEL in ('EASY', 'MEDIUM', 'HARD')),
	CORRECT_OPTION_ID bigint
);

create table QUESTION_OPTIONS(
	OPTION_ID bigserial primary key,
	QUESTION_ID bigint not null,
	OPTION_LABEL varchar(5) not null, -- 'A', 'B', 'C', 'D',
	OPTION_TEXT text not null,
	CREATED_AT timestamp with time zone default current_timestamp not null,
	UPDATED_AT timestamp with time zone default current_timestamp not null,
	constraint fk_QUESTION_ID foreign key (QUESTION_ID) references QUESTIONS(QUESTION_ID)
);

create table EXAM_SESSIONS(
	SESSION_ID bigserial primary key,
	USER_ID bigint not null unique,
	START_TIME timestamp with time zone default current_timestamp not null,
	END_TIME timestamp with time zone,
	STATUS varchar(20) not null check(STATUS in ('IN_PROGRESS', 'COMPLETED'))
);

create table STUDENT_ANSWERS(
	ANSWER_ID bigserial primary key,
	SESSION_ID bigint not null,
	QUESTION_ID bigint not null,
	SELECTED_OPTION_ID bigint not null,
	IS_ANSWER_CORRECT boolean,
	SUBMITTED_AT timestamp with time zone not null default current_timestamp,
	constraint fk_SESSION_ID foreign key (SESSION_ID) references EXAM_SESSIONS(SESSION_ID),
	constraint fk_QUESTION_ID foreign key (QUESTION_ID) references QUESTIONS(QUESTION_ID),
	constraint fk_SELECTED_OPTION_ID foreign key (SELECTED_OPTION_ID) references QUESTION_OPTIONS(OPTION_ID),
	-- a Question must be answered within given session only, same Question in multiple sessions not allowed
	constraint uk_QUESTION_SESSION unique(QUESTION_ID, SESSION_ID)
);
