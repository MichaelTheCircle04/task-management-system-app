package com.mtrifonov.task.management.system.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentDTO {
	
	private long id;
	private long taskId;
	private String author;
	private String text;
}
