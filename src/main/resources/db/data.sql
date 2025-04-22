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