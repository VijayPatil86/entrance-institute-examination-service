alter table QUESTIONS add column CREATED_AT timestamp with time zone default current_timestamp not null;
alter table QUESTIONS add column UPDATED_AT timestamp with time zone default current_timestamp not null;

alter table QUESTIONS add constraint fk_CORRECT_OPTION_ID foreign key (CORRECT_OPTION_ID) references QUESTION_OPTIONS(OPTION_ID);
alter table QUESTIONS add column IS_ACTIVE boolean not null default true;
