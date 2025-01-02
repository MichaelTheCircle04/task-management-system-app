package com.mtrifonov.task.management.system.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;

import com.mtrifonov.task.management.system.app.assemblers.TaskCommentPagedResourcesAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskPagedResourcesAssembler;
import com.mtrifonov.task.management.system.app.entities.Task;

@SpringBootApplication
public class Application {
	
    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public TaskPagedResourcesAssembler taskPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver) {
    	
    	return new TaskPagedResourcesAssembler(resolver, null);
    }
    
    @Bean
    public TaskCommentPagedResourcesAssembler taskCommentPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver) {
    	
    	return new TaskCommentPagedResourcesAssembler(resolver, null);
    }
    
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver hateoasPageableHandlerMethodArgumentResolver() {
    	
    	var sortResolver = new HateoasSortHandlerMethodArgumentResolver();
    	sortResolver.setSortParameter("sortParams");
    	var resolver = new HateoasPageableHandlerMethodArgumentResolver();
    	resolver.setPageParameterName("pageNum");
    	resolver.setSizeParameterName("pageSize");
    	return resolver;
    }
}
