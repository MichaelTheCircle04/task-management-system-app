package com.mtrifonov.task.management.system.app.controllers;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.services.TaskService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("task/management/system")
@RequiredArgsConstructor
public class SystemController {
	
	private final TaskService taskService;
	private final TaskModelAssembler taskAssembler;
	private final TaskCommentModelAssembler taskCommentAssembler;
	private final TaskPagedResourcesAssembler pagedResourcesAssemble;
	private final TaskCommentPagedResourcesAssembler pagedCommentResourcesAssemble;
	@Value("${server.advertised-adress}")
	private String adress;

	//Return a task with a given ID
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<TaskDTO>> getTaskById(@PathVariable long id, 
			Authentication user) {
		
		var task = taskService.getTaskById(id, user);
		return ResponseEntity.ok(taskAssembler.toModel(task));
	}
	
	//Return all tasks where the author is the user with a given EMAIL
	@GetMapping("/author/{email}")
	public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getAllTasksByAuthor(@PathVariable @Email String email, 
			@PageableDefault(sort = {"id", "desc"}) Pageable pageable/*,
			@RequestParam(defaultValue = "") String[] sortParams,
		    @RequestParam(defaultValue = "0") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize*/) {
		
		//System.out.println(sortParams.length);
		pageable.getSort().get().forEach(o -> System.out.println(o.getProperty()));
		var tasks = taskService.getAllTasksByAuthor(email, pageable/*, pageNum, pageSize, sortParams*/);
		var data = pagedResourcesAssemble.toModel(tasks, taskAssembler); 
		return ResponseEntity.ok(data);
	}
	
	//Return all tasks where the executor is the user with a given EMAIL
	@GetMapping("/executor/{email}")
	public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getAllTasksByExecutor(@PathVariable @Email String email,
			@RequestParam(defaultValue = "") String[] sortParams,
			@RequestParam(defaultValue = "0") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize) {
		
		var tasks = taskService.getAllTasksByExecutor(email, pageNum, pageSize, sortParams);
		var data = pagedResourcesAssemble.toModel(tasks, taskAssembler); 
		return ResponseEntity.ok(data);

	}
	
	//Return all comments related to task with given ID
	@GetMapping("/{id}/comments")
	public ResponseEntity<PagedModel<EntityModel<TaskCommentDTO>>> getAllComments(@PathVariable long id, Authentication user,
			@RequestParam(defaultValue = "") String[] sortParams,
			@RequestParam(defaultValue = "0") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize) {
		
		var comments = taskService.getAllComments(id, user, pageNum, pageSize, sortParams);
		var model = pagedCommentResourcesAssemble.toModel(comments, taskCommentAssembler);
		model.add(Link.of("Task " + id + ": ", "http://" + adress + "/task/management/system/" + id));
		return ResponseEntity.ok(model);
	}
	
	//Return all comments related to task with given ID and posted by user with given EMAIL 
	@GetMapping("/{id}/comments/{email}")
	public ResponseEntity<PagedModel<TaskCommentDTO>> getAllCommentsByAuthor(@PathVariable long id, 
			@PathVariable @Email String email, Authentication user,
			@RequestParam(defaultValue = "") String[] sortParams,
			@RequestParam(defaultValue = "0") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize) {
		
		var comments = taskService.getAllCommentsByAuthor(id, email, user, pageNum, pageSize, sortParams);
				
		var data = comments.stream().map(c -> { 
						c.setTask(Task.builder().id(id).build());
						return c; 
						}).map(taskCommentAssembler::toDTO).toList();
		
		var model = PagedModel.of(data, 
				new PagedModel.PageMetadata(
						comments.getSize(),
						comments.getNumber(),
						comments.getTotalElements())
				);
				
		model.add(Link.of("Task " + id + ": ", "http://" + adress + "/task/management/system/" + id));
		return ResponseEntity.ok(model);
	}
	
	//Create task with given body
	@PostMapping("/create")
	public ResponseEntity<Void> createTask(@Valid @RequestBody TaskDTO data, 
			Authentication creator) {
		
		var task = taskService.createTask(data, creator);
		return ResponseEntity.created(URI.create("http://" + adress + "/task/management/system/" + task.getId())).build();
	}
	
	//Delete the task with a given ID
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteTask(@PathVariable long id) {
		
		taskService.deleteTaskById(id);
		return ResponseEntity.ok().build();
	}
	
	//Change the status of a task with a given ID
	@PutMapping("/{id}/status")
	public ResponseEntity<Void> updateStatus(@PathVariable long id, 
			@RequestParam Status newStatus, 
			Authentication user) {
		
		taskService.updateStatus(id, newStatus, user);
		return ResponseEntity.ok().build();
	}
	
	//Change the priority of a task with a given ID
	@PutMapping("/{id}/priority")
	public ResponseEntity<Void> updatePriority(@PathVariable long id, 
			@RequestParam Priority newPriority) {
		
		taskService.updatePriority(id, newPriority);
		return ResponseEntity.ok().build();
	}
	
	//Set the user with the given EMAIL as the executor of the task with the given ID
	@PutMapping("/{id}/executor")
	public ResponseEntity<Void> setExecutor(@PathVariable long id, 
			@RequestParam @Email String email) {
		
		taskService.setExecutort(id, email);
		return ResponseEntity.ok().build();
	}
	
	//Add a comment with a given TEXT to a task with a given ID
	@PostMapping("/{id}/comment")
	public ResponseEntity<Void> addComment(@PathVariable long id,
			@RequestBody String text, Authentication user) {
		
		var comment = taskService.addComment(id, text, user);
		return ResponseEntity.created(URI.create("http://" + adress + "/task/management/system/" + id + "/comment" + comment.getId())).build();
	}
}
