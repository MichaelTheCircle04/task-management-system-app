package com.mtrifonov.task.management.system.app.services;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtrifonov.task.management.system.app.dto.TaskDTO;
import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.repositories.TaskCommentRepository;
import com.mtrifonov.task.management.system.app.repositories.TaskRepository;
import com.mtrifonov.task.management.system.app.entities.TaskComment;

@Service
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final TaskCommentRepository taskCommentRepository;
		
	public TaskService(TaskRepository taskRepository, TaskCommentRepository taskCommentRepository) {
		this.taskRepository = taskRepository;
		this.taskCommentRepository = taskCommentRepository;
	}
	
	public Task getTaskById(Long id, Authentication user) {
		
		var task = this.getTaskById(id);
		this.checkAccess("ROLE_ADMIN", task, user, "You do not have enough rights to get the task");		
		return task;
	}
	
	public Page<Task> getAllTasksByAuthor(String email, int pageNum,
			int pageSize, String[] sortParams) {
		
		var pagable = this
				.createPageRequest(pageNum, pageSize, sortParams);
		return taskRepository.findAllByAuthor(email, pagable);
	}
	
	public Page<Task> getAllTasksByExecutor(String email, int pageNum, int pageSize, 
			String[] sortParams) {
		
		var pagable = this
				.createPageRequest(pageNum, pageSize, sortParams);
		return taskRepository.findAllByExecutor(email, pagable);
	}
	
	public Page<TaskComment> getAllComments(long id, Authentication user, int pageNum,
			int pageSize, String[] sortParams) {
		
		var task = this.getTaskById(id);
		this.checkAccess("ROLE_ADMIN", task, user, "You do not have enough rights to get comments of task");	
		var pagable = this
				.createPageRequest(pageNum, pageSize, sortParams);
		return taskCommentRepository.findAllByTask(id, pagable);
	}
	
	public Page<TaskComment> getAllCommentsByAuthor(long id, String email,
			Authentication user, int pageNum,
			int pageSize, String[] sortParams) {
		
		var task = this.getTaskById(id);
		this.checkAccess("ROLE_ADMIN", task, user, "You do not have enough rights to get comments of task");	
		var pagable = this
				.createPageRequest(pageNum, pageSize, sortParams);
		return taskCommentRepository.findAllByTaskAndAuthor(id, email, pagable);
	}
	
	public Task createTask(TaskDTO data, Authentication creator) {
		
		var principal = (ClaimAccessor) creator.getPrincipal();
		var task = Task.builder()
				.header(data.getHeader())
				.description(data.getDescription())
				.status(Status.WAITING)
				.priority(data.getPriority())
				.author(principal.getClaimAsString("email"))
				.build();
		
		return taskRepository.save(task);
	}
	
	public void deleteTaskById(Long id) {
		
		taskRepository.deleteById(id);
	}
	
	@Transactional
	public void updateStatus(long id, Status newStatus, Authentication user) {
		
		var task = this.getTaskById(id);
		this.checkAccess("ROLE_ADMIN", task, user, "You do not have enough rights to change the task");
		task.setStatus(newStatus);
	}
	
	@Transactional
	public void updatePriority(long id, Priority newPriority) {
		
		var task = this.getTaskById(id);
		task.setPriority(newPriority);
	}
	
	@Transactional
	public void setExecutort(long id, String email) {
		
		var task = this.getTaskById(id);
		task.setExecutor(email);
	}
	
	@Transactional
	public TaskComment addComment(long id, String text, Authentication user) {
		
		var task = this.getTaskById(id);		
		this.checkAccess("ROLE_ADMIN", task, user, "You do not have enough rights to add comments to task");		
		var principal = (ClaimAccessor) user.getPrincipal();
		var taskComment = TaskComment.builder()
				.task(task)
				.text(text)
				.author(principal.getClaimAsString("email"))
				.build();
		
		return taskCommentRepository.save(taskComment);
	}
	
	private Task getTaskById(long id) {
		return taskRepository
				.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Can't find task with id: " + id));
	}
	
	private void checkAccess(String requiredRole, Task task, 
			Authentication user, String errorMessage) {
		
		var principal = (ClaimAccessor) user.getPrincipal();
		
		if (!principal.getClaimAsString("email").equals(task.getExecutor()) && 
				!principal.getClaimAsString("email").equals(task.getAuthor()) && 
				!user.getAuthorities().contains((GrantedAuthority) new SimpleGrantedAuthority(requiredRole))) {
			throw new AccessDeniedException(errorMessage + " : " + task.getId());
		}
	}
	
	private PageRequest createPageRequest(int pageNum, int pageSize, String[] sortParams) {
		
		PageRequest pagable;
		
		if (sortParams.length == 0) {
			pagable = PageRequest.of(pageNum, pageSize, Sort.unsorted());
		} else {
			var direction = sortParams[sortParams.length - 1].equals("desc") ? Direction.DESC : Direction.ASC; 
			var args = Arrays.copyOfRange(sortParams, 0, sortParams.length - 1);
			var sort = Sort.by(direction, args);
			pagable = PageRequest.of(pageNum, pageSize, sort);
		}
		
		return pagable;
	}
}
