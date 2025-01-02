package com.mtrifonov.task.management.system.app.assemblers;

import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.util.UriComponents;

import com.mtrifonov.task.management.system.app.entities.Task;

public class TaskPagedResourcesAssembler extends PagedResourcesAssembler<Task> {

	public TaskPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver, UriComponents baseUri) {
		super(resolver, baseUri);
	}

}
