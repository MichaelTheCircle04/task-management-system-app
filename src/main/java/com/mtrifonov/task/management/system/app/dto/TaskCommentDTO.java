package com.mtrifonov.task.management.system.app.dto;

import com.mtrifonov.task.management.system.app.entities.TaskComment;
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
public class TaskCommentDTO {
	
	private Long id;
	private String author;
	private String text;

	public static TaskCommentDTO toDTO(TaskComment c) {
		
		return new TaskCommentDTO(
			c.getId(),
			c.getAuthor(),
			c.getText()); 
	}
}
