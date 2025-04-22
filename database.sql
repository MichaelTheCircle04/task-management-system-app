CREATE SEQUENCE IF NOT EXISTS public.tasks_task_id_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.tasks_task_id_seq
    OWNER TO postgres;

CREATE SEQUENCE IF NOT EXISTS public.task_comments_task_comment_id_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.task_comments_task_comment_id_seq
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.tasks
(
    task_id bigint NOT NULL DEFAULT nextval('tasks_task_id_seq'::regclass),
    header character varying COLLATE pg_catalog."default" NOT NULL,
    description character varying COLLATE pg_catalog."default" NOT NULL,
    status character varying COLLATE pg_catalog."default" NOT NULL,
    author character varying COLLATE pg_catalog."default" NOT NULL,
    executor character varying COLLATE pg_catalog."default",
    priority character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT tasks_pkey PRIMARY KEY (task_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tasks
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.task_comments
(
    task_comment_id bigint NOT NULL DEFAULT nextval('task_comments_task_comment_id_seq'::regclass),
    task_id bigint NOT NULL,
    text character varying COLLATE pg_catalog."default" NOT NULL,
    author character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT task_comments_pkey PRIMARY KEY (task_comment_id),
    CONSTRAINT task_comments_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks (task_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.task_comments
    OWNER to postgres;

ALTER SEQUENCE public.tasks_task_id_seq
    OWNED BY public.tasks.task_id;

ALTER SEQUENCE public.task_comments_task_comment_id_seq
    OWNED BY public.task_comments.task_comment_id;

INSERT INTO tasks(header, description, status, author, executor, priority) 
    VALUES ('header', 'task1', 'COMPLETED', 's.spiegel@example.com', 'b.baggins@example.com', 'MIDDLE'), 
           ('header', 'task2', 'IN_PROGRESS', 's.spiegel@example.com', 'm.circle@example.com', 'MIDDLE'), 
           ('header', 'task3', 'WAITING', 's.spiegel@example.com', 'm.circle@example.com', 'LOW'), 
           ('header', 'task4', 'COMPLETED', 's.spiegel@example.com', 'm.circle@example.com', 'MIDDLE'), 
           ('header', 'task5', 'IN_PROGRESS', 's.spiegel@example.com', 'b.baggins@example.com', 'HIGH');
           
INSERT INTO task_comments(task_id, text, author)
    VALUES (1, 'comment1', 's.spiegel@example.com'),
           (1, 'comment2', 'b.baggins@example.com'),
           (1, 'comment3', 'b.baggins@example.com'),
           (1, 'comment4', 'b.baggins@example.com'),
           (1, 'comment5', 'm.circle@example.com');

ALTER SEQUENCE tasks_task_id_seq INCREMENT BY 50;
ALTER SEQUENCE task_comments_task_comment_id_seq INCREMENT BY 50;