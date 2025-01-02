 package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.mtrifonov.task.management.system.app.dto.TaskCommentDTO;
import com.mtrifonov.task.management.system.app.entities.TaskComment;

@Component
public class TaskCommentModelAssembler implements RepresentationModelAssembler<TaskComment, EntityModel<TaskCommentDTO>> {
	
	@Value("${server.advertised-adress}")
	private String adress;
	
	@Override
	public EntityModel<TaskCommentDTO> toModel(TaskComment c) {
		
		var model = EntityModel.of(toDTO(c));
		var template = UriTemplate.of("http://" + adress + "/task/management/system/{var1}/comments/{var2}");
		model.add(Link.of(template, "All author comments: ").expand(c.getTask().getId(), c.getAuthor()));
		return model;
	}
	
	public TaskCommentDTO toDTO(TaskComment c) {
		return new TaskCommentDTO(
				c.getId(),
				c.getTask().getId(),
				c.getAuthor(),
				c.getText()); 
	}
	
}
