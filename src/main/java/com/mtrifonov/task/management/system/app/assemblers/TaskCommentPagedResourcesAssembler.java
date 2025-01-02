package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.util.UriComponents;

import com.mtrifonov.task.management.system.app.entities.TaskComment;

public class TaskCommentPagedResourcesAssembler extends PagedResourcesAssembler<TaskComment> {

	public TaskCommentPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver, UriComponents baseUri) {
		super(resolver, baseUri);
	}

}
