package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.mtrifonov.task.management.system.app.dto.TaskDTO;
import com.mtrifonov.task.management.system.app.entities.Task;

/**
*
* @Mikhail Trifonov
*/
@Component
public class TaskModelAssembler implements RepresentationModelAssembler<Task, EntityModel<TaskDTO>>{

	@Value("${server.advertised-address}")
	private String address;
	
	@Override
	public EntityModel<TaskDTO> toModel(Task t) {
		
		var model = EntityModel.of(TaskDTO.toDTO(t));

		var self = Link.of("http://" + address + "/task/management/system/" + t.getId(), IanaLinkRelations.SELF); //на себя
		model.add(self);
		
		var template = UriTemplate.of("http://" + address + "/task/management/system/{var1}/{var2}");
		model.add(Link.of(template, "All tasks created by: " + t.getAuthor()).expand("author", t.getAuthor())); //все задачи этого автора

		if (t.getExecutor() != null) {
			model.add(Link.of(template, "All tasks executed by: " + t.getExecutor()).expand("executor", t.getExecutor())); //все задачи этого исполнителя
		}
		
		model.add(Link.of(template, "Comments").expand(t.getId(), "comments")); //комментарии
		model.add(Link.of(template, "Delete this task").expand(t.getId(), "delete")); //удалить
		return model;
	}
}
