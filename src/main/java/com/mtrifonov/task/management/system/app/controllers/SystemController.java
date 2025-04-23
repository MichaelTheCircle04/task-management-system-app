package com.mtrifonov.task.management.system.app.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mtrifonov.task.management.system.app.assemblers.TaskCommentModelAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskCommentPagedResourcesAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskModelAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskPagedResourcesAssembler;
import com.mtrifonov.task.management.system.app.dto.TaskCommentDTO;
import com.mtrifonov.task.management.system.app.dto.TaskDTO;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.services.TaskService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

/**
*
* @Mikhail Trifonov
*/
@Controller
@RequestMapping("/task/management/system")
@RequiredArgsConstructor
public class SystemController {
	
	private final TaskService taskService;
	private final TaskModelAssembler taskAssembler;
	private final TaskCommentModelAssembler taskCommentAssembler;
	private final TaskPagedResourcesAssembler pagedResourcesAssemble;
	private final TaskCommentPagedResourcesAssembler pagedCommentResourcesAssemble;
	@Value("${server.advertised-address}")
	private String address;

	//Return a task with a given ID
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<TaskDTO>> getTaskById(@PathVariable Long id, //covered
		JwtAuthenticationToken user) {
		
		var task = taskService.getTaskById(id, user);
		return ResponseEntity.ok(taskAssembler.toModel(task));
	}

	//Return a comment with a given ID
	@GetMapping("/comments/{id}")
	public ResponseEntity<EntityModel<TaskCommentDTO>> getCommentById(@PathVariable Long id, //covered
		JwtAuthenticationToken user) {

		var comment = taskService.getCommentById(id, user);
		var model = taskCommentAssembler.toModel(comment);
		model.add(Link.of("http://" + address + "/task/management/system/" + id, "Task " + comment.getTask().getId()));
		return ResponseEntity.ok(model);

	}
	
	//Return all tasks where the author is the user with a given EMAIL
	@GetMapping("/author/{email}")
	public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getAllTasksByAuthor(@PathVariable @Email String email, //covered
		@PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable) {
		
		var tasks = taskService.getAllTasksByAuthor(email, pageable);
		var data = pagedResourcesAssemble.toModel(tasks, taskAssembler); 
		return ResponseEntity.ok(data);
	}
	
	//Return all tasks where the executor is the user with a given EMAIL
	@GetMapping("/executor/{email}")
	public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getAllTasksByExecutor(@PathVariable @Email String email, //covered
		@PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable) {
		
		var tasks = taskService.getAllTasksByExecutor(email, pageable);
		var data = pagedResourcesAssemble.toModel(tasks, taskAssembler); 
		return ResponseEntity.ok(data);

	}

	//Return all comments related to task with given ID
	@GetMapping("/{id}/comments")
	public ResponseEntity<PagedModel<EntityModel<TaskCommentDTO>>> getAllComments(@PathVariable Long id, JwtAuthenticationToken user, //covered
		@PageableDefault(sort = {"task_comment_id"}, direction = Direction.ASC) Pageable pageable) {
		
		var comments = taskService.getAllComments(id, user, pageable);
		var model = pagedCommentResourcesAssemble.toModel(comments, taskCommentAssembler);
		model.add(Link.of("http://" + address + "/task/management/system/" + id, "Task " + id));
		return ResponseEntity.ok(model);
	}
	
	//Return all comments related to task with given ID and posted by user with given EMAIL 
	@GetMapping("/{id}/comments/{email}")
	public ResponseEntity<PagedModel<EntityModel<TaskCommentDTO>>> getAllCommentsByAuthor(@PathVariable Long id, //covered
		@PathVariable @Email String email, JwtAuthenticationToken user,
		@PageableDefault(sort = {"task_comment_id"}, direction = Direction.ASC) Pageable pageable) {
		
		var comments = taskService.getAllCommentsByAuthor(id, email, user, pageable);
		var model = pagedCommentResourcesAssemble.toModel(comments, taskCommentAssembler);
		model.add(Link.of("http://" + address + "/task/management/system/" + id, "Task " + id));
		return ResponseEntity.ok(model);
	}
	
	//Create task with given body
	@PostMapping("/create")
	public ResponseEntity<Void> createTask(@Valid @RequestBody TaskDTO data, //covered
		JwtAuthenticationToken creator) {
		
		var task = taskService.createTask(data, creator);
		return ResponseEntity.created(URI.create("http://" + address + "/task/management/system/" + task.getId())).build();
	}

		
	//Add a comment with a given TEXT to a task with a given ID
	@PostMapping("/{id}/comment")
	public ResponseEntity<Void> addComment(@PathVariable Long id, //covered
			@RequestBody String text, JwtAuthenticationToken user) {
		
		var comment = taskService.addComment(id, text, user);
		return ResponseEntity.created(URI.create("http://" + address + "/task/management/system/comments/" + comment.getId())).build();
	}
	
	//Change the status of a task with a given ID
	@PutMapping("/{id}/status")
	public ResponseEntity<Void> updateStatus(@PathVariable Long id, //covered
		@RequestParam Status newStatus, 
		JwtAuthenticationToken user) {
		
		taskService.updateStatus(id, newStatus, user);
		return ResponseEntity.ok().build();
	}
	
	//Change the priority of a task with a given ID
	@PutMapping("/{id}/priority")
	public ResponseEntity<Void> updatePriority(@PathVariable Long id, //covered
			@RequestParam Priority newPriority) {
		
		taskService.updatePriority(id, newPriority);
		return ResponseEntity.ok().build();
	}
	
	//Set the user with the given EMAIL as the executor of the task with the given ID
	@PutMapping("/{id}/executor")
	public ResponseEntity<Void> setExecutor(@PathVariable Long id, //covered
			@RequestParam @Email String email) {
		
		taskService.setExecutort(id, email);
		return ResponseEntity.ok().build();
	}

	//Delete the task with a given ID
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) { //covered
			
		taskService.deleteTaskById(id);
		return ResponseEntity.ok().build();
	}
}
