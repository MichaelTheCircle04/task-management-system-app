package com.mtrifonov.task.management.system.app.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.mtrifonov.task.management.system.app.Application;
import com.mtrifonov.task.management.system.app.assemblers.TaskCommentModelAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskModelAssembler;
import com.mtrifonov.task.management.system.app.configs.SecurityConfig;
import com.mtrifonov.task.management.system.app.controllers.SystemController;
import com.mtrifonov.task.management.system.app.controllers.SystemControllerAdvice;
import com.mtrifonov.task.management.system.app.security.WithCustomUser;
import com.mtrifonov.task.management.system.app.services.TaskService;
import com.mtrifonov.task.management.system.app.stubs.StubTaskCommentRepository;
import com.mtrifonov.task.management.system.app.stubs.StubTaskRepository;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.text.MatchesPattern.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
@WebMvcTest(controllers = SystemController.class)
@ContextConfiguration(classes = { Application.class,
		TaskService.class, TaskModelAssembler.class, SystemController.class, SecurityConfig.class,
		TaskCommentModelAssembler.class, SystemControllerAdvice.class, 
		StubTaskRepository.class, StubTaskCommentRepository.class
		})
//@TestMethodOrder(MethodOrderer.MethodName.class)
public class SystemControllerTest {
	
	@Autowired
	WebApplicationContext ctx;
	
	MockMvc mvc;
	
	@BeforeEach
	void setup() {

		this.mvc = MockMvcBuilders
				.webAppContextSetup(ctx)
				.apply(springSecurity())
				.build();
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void createNewTask_validRequest_statusCreated() throws Exception {
			
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/create")
				.content("{\"header\": \"header\", \"description\": \"task\", \"priority\": \"HIGH\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf());
		
		mvc.perform(requestBuilder)
				.andExpect(status().isCreated())
				.andExpect(header().string("location", 
						matchesPattern("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/task\\/management\\/system\\/\\d+)$")));
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void createNewTask_invalidRequest_statusBadRequest() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/create")
				.content("{\"headerRR\": \"header\", \"description\": \"task\", \"priority\": \"HIGH\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf());
		
		var result = mvc.perform(requestBuilder)
				.andExpect(status().isBadRequest())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Header must be present"));	
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getTask_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/2");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getTask_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void getTask_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void getTask_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getAllTasksByAuthor_valideRequestWithAdminRole_statusOk() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/author/s.spiegel@example.com")
				.params(MultiValueMap.fromMultiValue(params));
		
		var result = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		
		log.info(result.getResponse().getContentAsString());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllTasksByAuthor_validRequestWithUserRole_statusForbidden() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/author/s.spiegel@example.com")
				.params(MultiValueMap.fromMultiValue(params));
				
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
		
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllComments_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1/comments")
				.params(MultiValueMap.fromMultiValue(params));
		
		
		var res = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		log.info(res.getResponse().getContentAsString());
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void getAllComments_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1/comments")
				.params(MultiValueMap.fromMultiValue(params));
		
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	void getAllComments_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1/comments")
				.params(MultiValueMap.fromMultiValue(params));
		
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void getAllComments_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var params = Map.of(
				"sortParams", List.of("id,asc"), 
				"pageNum", List.of("1"),
				"pageSize", List.of("1"));
		
		var requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/1/comments")
				.params(MultiValueMap.fromMultiValue(params));
		
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void addComment_validRequestAuthorPerformingWithUserRole_statusCreated() throws Exception {
		
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isCreated());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void addComment_validRequestExecutorPerformingWithUserRole_statusCreated() throws Exception {
		
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isCreated());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	void addComment_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusCreated() throws Exception {
		
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isCreated());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void addComment_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}
	
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void addComment_invalidRequestAuthorPerformingWithUserRole_statusBadRequest() throws Exception {
		
		
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/1/comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		
		mvc.perform(requestBuilder)
			.andExpectAll(
					status().isBadRequest(), 
					content().string(containsString("Error deserializing request body: "))
					);
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void deleteCreatedTask_validRequest_statusOk() throws Exception {
		
		var requestBuilder = MockMvcRequestBuilders
				.delete("/task/management/system/3/delete")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
}
