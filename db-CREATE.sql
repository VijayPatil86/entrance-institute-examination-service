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

-- This table stores the specific list of questions assigned to a student for their exam session.
-- It ensures that every student gets a consistent set of questions, even if they disconnect and reconnect.
create table SESSION_QUESTIONS(
	SESSION_QUESTION_ID bigserial primary key,
	SESSION_ID bigint not null, -- mapped to STUDENT_ID in table EXAM_SESSIONS
	QUESTION_ID bigint not null,
	SEQUENCE_NUMBER int not null, -- The order in which the question appears in the exam (0, 1, 2, ...)
	constraint fk_SESSION_ID foreign key (SESSION_ID) references EXAM_SESSIONS(SESSION_ID),
	constraint fk_QUESTION_ID foreign key(QUESTION_ID) references QUESTIONS(QUESTION_ID),
	constraint unique_SESSION_ID_QUESTION_ID unique (SESSION_ID, QUESTION_ID), -- A question can only appear once per session
	constraint unique_SESSION_ID_SEQUENCE_NUMBER unique (SESSION_ID, SEQUENCE_NUMBER) -- Each question must have a unique order in the session
);

create table EXAM_RESULTS(
	RESULT_ID bigserial primary key,
	SESSION_ID bigint not null,		-- refers to specific session that generated the result
	USER_ID bigint not null,		-- for quick lookups
	SCORE int not null,
	TOTAL_QUESTIONS int not null,
	CORRECT_ANSWERS int not null,
	INCORRECT_ANSWERS int not null,
	EXAM_RANK int, 	-- nullable as it may be calculated in a separate step after initial grading,
	RESULT_PUBLISH_DATE timestamp with time zone,	-- nullable as results may be held until an official announcement date
	CREATED_AT timestamp with time zone not null default current_timestamp,
	UPDATED_AT timestamp with time zone not null default current_timestamp,
	constraint unique_SESSION_ID unique (SESSION_ID),
	constraint fk_SESSION_ID foreign key (SESSION_ID) references EXAM_SESSIONS(SESSION_ID)
);

-- Optional: add an index on user_id for fast lookups of a user's result.
create index idx_EXAM_RESULTS_USER_ID on EXAM_RESULTS(USER_ID);

