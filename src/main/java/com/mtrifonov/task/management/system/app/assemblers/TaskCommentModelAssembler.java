 package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.mtrifonov.task.management.system.app.dto.TaskCommentDTO;
import com.mtrifonov.task.management.system.app.entities.TaskComment;

/**
*
* @Mikhail Trifonov
*/
@Component
public class TaskCommentModelAssembler implements RepresentationModelAssembler<TaskComment, EntityModel<TaskCommentDTO>> {
	
	@Value("${server.advertised-address}")
	private String address;
	
	@Override
	public EntityModel<TaskCommentDTO> toModel(TaskComment c) {

		var model = EntityModel.of(TaskCommentDTO.toDTO(c));

		var self = Link.of("http://" + address + "/task/management/system/comments/" + c.getId(), IanaLinkRelations.SELF); //на себя
		model.add(self);

		var template = UriTemplate.of("http://" + address + "/task/management/system/{var1}/comments/{var2}");
		model.add(Link.of(template, "All author comments").expand(c.getTask().getId(), c.getAuthor())); //все комментарии к этой задаче от автора этого комментария
		
		return model;
	}
}
