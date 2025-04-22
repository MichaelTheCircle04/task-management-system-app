SET MODE PostgreSQL;

CREATE SEQUENCE IF NOT EXISTS tasks_task_id_seq
    INCREMENT BY 1 
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS tasks
(
    task_id bigint NOT NULL DEFAULT nextval('tasks_task_id_seq'),
    header character varying NOT NULL,
    description character varying NOT NULL,
    status character varying NOT NULL,
    author character varying NOT NULL,
    executor character varying,
    priority character varying NOT NULL,
    CONSTRAINT tasks_pkey PRIMARY KEY (task_id)
);

CREATE SEQUENCE IF NOT EXISTS task_comments_task_comment_id_seq
    INCREMENT BY 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS task_comments
(
    task_comment_id bigint NOT NULL DEFAULT nextval('task_comments_task_comment_id_seq'),
    task_id bigint NOT NULL,
    text character varying NOT NULL,
    author character varying NOT NULL,
    CONSTRAINT task_comments_pkey PRIMARY KEY (task_comment_id),
    CONSTRAINT task_comments_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks (task_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);