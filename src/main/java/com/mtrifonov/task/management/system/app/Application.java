package com.mtrifonov.task.management.system.app;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mtrifonov.task.management.system.app.assemblers.TaskCommentPagedResourcesAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskPagedResourcesAssembler;

@SpringBootApplication
public class Application implements WebMvcConfigurer {
	
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
    	
    	var resolver = new HateoasPageableHandlerMethodArgumentResolver(sortResolver);
    	resolver.setPageParameterName("pageNum");
    	resolver.setSizeParameterName("pageSize");
    	return resolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(hateoasPageableHandlerMethodArgumentResolver());
    }
}
