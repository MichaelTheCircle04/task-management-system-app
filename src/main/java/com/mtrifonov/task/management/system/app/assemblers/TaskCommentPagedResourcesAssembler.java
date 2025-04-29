package com.mtrifonov.task.management.system.app.assemblers;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.util.UriComponents;
import com.mtrifonov.task.management.system.app.entities.TaskComment;
import jakarta.servlet.http.HttpServletRequest;

/**
*
* @Mikhail Trifonov
*/
public class TaskCommentPagedResourcesAssembler extends PagedResourcesAssembler<TaskComment> {
	
	public TaskCommentPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver, UriComponents baseUri) {
		super(resolver, baseUri);
	}
}
