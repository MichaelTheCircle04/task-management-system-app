package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.mtrifonov.task.management.system.app.dto.TaskDTO;
import com.mtrifonov.task.management.system.app.entities.Task;

@Component
public class TaskModelAssembler implements RepresentationModelAssembler<Task, EntityModel<TaskDTO>>{

	@Value("${server.advertised-adress}")
	private String adress;
	
	@Override
	public EntityModel<TaskDTO> toModel(Task t) {
		
		var model = EntityModel.of(toDTO(t));
		var template = UriTemplate.of("http://" + adress + "/task/management/system/{var1}/{var2}");
		model.add(Link.of(template, "All tasks created by: " + t.getAuthor()).expand("author", t.getAuthor()));
		if (t.getExecutor() != null) {
			model.add(Link.of(template, "All tasks executed by: " + t.getExecutor()).expand("executor", t.getExecutor()));
		}
		model.add(Link.of(template, "Comments: ").expand(t.getId(), "comments"));
		model.add(Link.of(template, "Delete this task: ").expand(t.getId(), "delete"));
		return model;
	}
	
	public TaskDTO toDTO(Task t) {
		return new TaskDTO(
				t.getId(), 
				t.getHeader(), 
				t.getDescription(),
				t.getStatus(),
				t.getPriority(),
				t.getAuthor(), 
				(t.getExecutor() == null ? "EXPECTED" : t.getExecutor())
				);
	}
}
