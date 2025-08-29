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

