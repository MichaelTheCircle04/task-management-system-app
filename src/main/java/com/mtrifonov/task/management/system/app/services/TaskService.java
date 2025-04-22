package com.mtrifonov.task.management.system.app.services;

import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mtrifonov.task.management.system.app.dto.TaskDTO;
import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.repositories.TaskCommentRepository;
import com.mtrifonov.task.management.system.app.repositories.TaskRepository;
import com.mtrifonov.task.management.system.app.entities.TaskComment;

/**
*
* @Mikhail Trifonov
*/
@Service
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final TaskCommentRepository taskCommentRepository;
		
	public TaskService(TaskRepository taskRepository, TaskCommentRepository taskCommentRepository) {
		this.taskRepository = taskRepository;
		this.taskCommentRepository = taskCommentRepository;
	}
	
	public Task getTaskById(Long id, JwtAuthenticationToken user) {
		
		var task = this.getTaskById(id);

		this.checkAccess(
			"ROLE_ADMIN", task, user, 
			"You do not have enough rights to get the task"
		);	

		return task;
	}

	public TaskComment getCommentById(Long id, JwtAuthenticationToken user) {
		
		var comment = this.getCommentById(id);

		this.checkAccess(
			"ROLE_ADMIN", comment.getTask(), user, 
			"You do not have enough rights to get comment of task"
		);

		return comment;
	}
		
	public Page<Task> getAllTasksByAuthor(String email, Pageable pageable) {
		return taskRepository.findAllByAuthor(email, pageable);
	}
	
	public Page<Task> getAllTasksByExecutor(String email, Pageable pageable) {
		return taskRepository.findAllByExecutor(email, pageable);
	}
	
	public Page<TaskComment> getAllComments(Long id, 
		JwtAuthenticationToken user, Pageable pageable) {

		Supplier<Page<Object[]>> repoCall = () -> {
			return taskCommentRepository.findByTaskId(id, pageable);
		};
		
		return obtainPage(repoCall, id, user);
	}
	
	public Page<TaskComment> getAllCommentsByAuthor(Long id, String email,
		JwtAuthenticationToken user, Pageable pageable) {

		Supplier<Page<Object[]>> repoCall = () -> {
			return taskCommentRepository.findByTaskIdAndAuthor(id, email, pageable);
		};

		return obtainPage(repoCall, id, user);
	}
	
	public Task createTask(TaskDTO data, JwtAuthenticationToken creator) {
		
		var jwt = creator.getToken();
		var task = Task.builder()
				.header(data.getHeader())
				.description(data.getDescription())
				.status(Status.WAITING)
				.priority(data.getPriority())
				.author(jwt.getClaimAsString("email"))
				.build();
		
		var created = taskRepository.save(task);
		return created;
	}
	
	public void deleteTaskById(Long id) {
		taskRepository.deleteById(id);
	}
	
	@Transactional
	public void updateStatus(Long id, Status newStatus, JwtAuthenticationToken user) {
		
		var task = this.getTaskById(id);

		this.checkAccess(
			"ROLE_ADMIN", task, user,
			"You do not have enough rights to change the task"
		);

		task.setStatus(newStatus);
	}
	
	@Transactional
	public void updatePriority(Long id, Priority newPriority) {
		
		var task = this.getTaskById(id);
		task.setPriority(newPriority);
	}
	
	@Transactional
	public void setExecutort(Long id, String email) {
		
		var task = this.getTaskById(id);
		task.setExecutor(email);
	}
	
	@Transactional
	public TaskComment addComment(Long id, String text, JwtAuthenticationToken user) {
		
		var task = this.getTaskById(id);

		this.checkAccess(
			"ROLE_ADMIN", task, user, 
			"You do not have enough rights to add comments to task"
		);		
		
		var principal = user.getToken();
		var taskComment = TaskComment.builder()
				.task(task)
				.text(text)
				.author(principal.getClaimAsString("email"))
				.build();
		
		return taskCommentRepository.save(taskComment);
	}
	
	private Task getTaskById(Long id) {

		return taskRepository
				.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Can't find task with id: " + id));
	}

	private TaskComment getCommentById(Long id) {

		return taskCommentRepository
			.findById(id)
			.orElseThrow(() -> new NoSuchElementException("Can't find comment with id: " + id));
	}
	
	private void checkAccess(String requiredRole, Task task, 
		JwtAuthenticationToken user, String errorMessage) {
		
		var principal = user.getToken();
		
		if (!principal.getClaimAsString("email").equals(task.getExecutor()) && 
				!principal.getClaimAsString("email").equals(task.getAuthor()) && 
				!user.getAuthorities().contains(new SimpleGrantedAuthority(requiredRole))) {
			throw new AccessDeniedException(errorMessage + ": " + task.getId());
		}
	}

	private Page<TaskComment> obtainPage(Supplier<Page<Object[]>> repoCall, Long id, JwtAuthenticationToken user) {

		var records = repoCall.get();
		var content = records.getContent(); //Page<Object[]> -> List<Object[]>

		Task task;
		
		if (content.isEmpty()) {
			task = this.getTaskById(id);
		} else {
			task = (Task) content.get(0)[0];
		}

		var comments = records.map(r -> { //Page<Object[]> -> List<TaskComment>
			var comment = (TaskComment) r[1];
			comment.setTask(task);
			return comment;
		}).toList();


		this.checkAccess(
			"ROLE_ADMIN", task, user,
			"You do not have enough rights to get comments of task"
		);

		return new PageImpl<>(comments, records.getPageable(), records.getTotalElements());
	}
}
