package com.mtrifonov.task.management.system.app.dto;

import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*
* @Mikhail Trifonov
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
	
	private Long id;
	@NotBlank(message = "Header must be present")
	private String header;
	@NotBlank(message = "Description must be present")
	private String description;
	private Status status;
	@NotNull(message = "Priority must be present")
	private Priority priority;
	private String author;
	private String executor;

	public static TaskDTO toDTO(Task t) {

		return TaskDTO.builder()
			.id(t.getId())
			.header(t.getHeader())
			.description(t.getDescription())
			.status(t.getStatus())
			.priority(t.getPriority())
			.author(t.getAuthor())
			.executor(t.getExecutor() == null ? "EXPECTED" : t.getExecutor())
			.build();
	}
}
																			