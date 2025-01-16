package com.mtrifonov.task.management.system.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.entities.TaskComment;
import com.mtrifonov.task.management.system.app.repositories.TaskCommentRepository;
import com.mtrifonov.task.management.system.app.repositories.TaskRepository;

@Component
@Profile({"initDB"})
public class PopulateDatabaseRunner implements CommandLineRunner {
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired 
	private TaskCommentRepository taskCommentRepo;

	@Override
	public void run(String... args) throws Exception {
		
		List<Task> tasks = new ArrayList<>(List.of(
			Task.builder()
			.header("header")
			.description("task1")
			.status(Status.COMPLETED)
			.priority(Priority.MIDDLE)
			.author("s.spiegel@example.com")
			.executor("b.baggins@example.com")
			.build(), 
			Task.builder()
			.header("header")
			.description("task2")
			.status(Status.IN_PROGRESS)
			.priority(Priority.MIDDLE)
			.author("s.spiegel@example.com")
			.executor("m.circle@example.com")
			.build(),
			Task.builder()
			.header("header")
			.description("task3")
			.status(Status.WAITING)
			.priority(Priority.LOW)
			.author("s.spiegel@example.com")
			.executor("m.circle@example.com")
			.build(),
			Task.builder()
			.header("header")
			.description("task4")
			.status(Status.COMPLETED)
			.priority(Priority.MIDDLE)
			.author("s.spiegel@example.com")
			.executor("m.circle@example.com")
			.build(),
			Task.builder()
			.header("header")
			.description("task5")
			.status(Status.IN_PROGRESS)
			.priority(Priority.HIGH)
			.author("s.spiegel@example.com")
			.executor("b.baggins@example.com")
			.build()
			));
		
		taskRepo.saveAll(tasks);
		
		List<TaskComment> comments = new ArrayList<>(List.of(
				TaskComment.builder()
				.task(Task.builder().id(1).build())
				.text("comment1")
				.author("s.spiegel@example.com").build(),
				TaskComment.builder()
				.task(Task.builder().id(1).build())
				.text("comment2")
				.author("b.baggins@example.com").build(),
				TaskComment.builder()
				.task(Task.builder().id(1).build())
				.text("comment3")
				.author("b.baggins@example.com").build(),
				TaskComment.builder()
				.task(Task.builder().id(1).build())
				.text("comment4")
				.author("b.baggins@example.com").build(),
				TaskComment.builder()
				.task(Task.builder().id(1).build())
				.text("comment5")
				.author("m.circle@example.com").build()
				));
		
		taskCommentRepo.saveAll(comments);
	}

}
