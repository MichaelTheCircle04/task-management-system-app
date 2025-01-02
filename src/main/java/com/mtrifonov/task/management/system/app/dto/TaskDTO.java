package com.mtrifonov.task.management.system.app.dto;

import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
	
	private Long id;
	@NotNull(message = "Header must be present")
	private String header;
	@NotNull(message = "Description must be present")
	private String description;
	private Status status;
	@NotNull(message = "Priority must be present")
	private Priority priority;
	private String author;
	private String executor;
}
																			