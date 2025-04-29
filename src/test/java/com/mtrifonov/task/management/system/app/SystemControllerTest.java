package com.mtrifonov.task.management.system.app;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.mtrifonov.task.management.system.app.Application;
import com.mtrifonov.task.management.system.app.assemblers.TaskCommentModelAssembler;
import com.mtrifonov.task.management.system.app.assemblers.TaskModelAssembler;
import com.mtrifonov.task.management.system.app.configs.SecurityConfig;
import com.mtrifonov.task.management.system.app.controllers.SystemController;
import com.mtrifonov.task.management.system.app.repositories.TaskCommentRepository;
import com.mtrifonov.task.management.system.app.repositories.TaskRepository;
import com.mtrifonov.task.management.system.app.security.WithCustomUser;
import com.mtrifonov.task.management.system.app.services.TaskService;
import jakarta.transaction.Transactional;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.MatchesPattern.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
*
* @Mikhail Trifonov
*/
@WebMvcTest(controllers = SystemController.class)
@AutoConfigureDataJpa
@ContextConfiguration(classes = 
	{ 
		Application.class, TaskService.class,
	    TaskModelAssembler.class, SecurityConfig.class, 
		TaskCommentModelAssembler.class,
		TaskRepository.class, TaskCommentRepository.class
	})
@ActiveProfiles(value = {"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
public class SystemControllerTest {
	
	MockMvc mvc;
	
	@MockitoBean
    ClientRegistrationRepository clientRegistrationRepository;
	@MockitoBean
	JwtDecoder jwtDecoder;

	@BeforeEach
	void setup(WebApplicationContext ctx) {

		mvc = MockMvcBuilders
				.webAppContextSetup(ctx)
				.apply(springSecurity())
				.build();
	}
	
	//GET /task/management/system/{id}
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com") //ADMIN -> OK
	void getTask_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/2");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getTask_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception { //EXECUTOR + USER -> OK
		
		var requestBuilder = get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER") //AUTHOR + USER -> OK
	void getTask_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER") //USER -> FORBIDDEN
	void getTask_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = get("/task/management/system/1");
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	//GET /task/management/system/comments/{id}
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com") 
	void getComment_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/comments/2");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getComment_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception { 
		
		var requestBuilder = get("/task/management/system/comments/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER") 
	void getComment_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/comments/1");
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER") 
	void getComment_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = get("/task/management/system/comments/1");
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER") 
	void getComment_invalidRequestNotAuthorOrExecutorPerformingWithAdminRole_statusBadRequest() throws Exception {
		
		var requestBuilder = get("/task/management/system/comments/8");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isBadRequest(),
				content().string("Can't find comment with id: 8")
			);
	}
	
	//GET /task/management/system/author/{email}
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getAllTasksByAuthor_valideRequestWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/author/s.spiegel@example.com")
				.param("sortParams", "executor,id,asc")
				.param("pageNum", "0")
				.param("pageSize", "5");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskList.length()", is(5)),
				jsonPath("$._embedded.taskList[1].description", is("task5")),
				jsonPath("$._embedded.taskList[4].description", is("task4"))
			);
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllTasksByAuthor_validRequestWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = get("/task/management/system/author/s.spiegel@example.com")
			.param("sortParam", "executor,id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
				
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
		
	}

	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getAllTasksByAuthor_invalidRequestWithAdminRole_statusBadRequest() throws Exception {
		
		var requestBuilder = get("/task/management/system/author/s.spiegel$example.com") //Not valid email
			.param("sortParam", "executor,id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
				
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isBadRequest(),
				content().string(containsString("Invalid argument cause exception:"))
			);	
	}
	
	//GET /task/management/system/executor/{email}
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getAllTasksByExecutor_validRequestWithAdminRole_statusOk() throws Exception {

		var requestBuilder = get("/task/management/system/executor/m.circle@example.com")
				.param("sortParams", "id,asc")
				.param("pageNum", "0")
				.param("pageSize", "5");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskList.length()", is(3)),
				jsonPath("$._embedded.taskList[0].description", is("task2")),
				jsonPath("$._embedded.taskList[2].description", is("task4"))
			);
	}

	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllTasksByExecutor_validRequestWithUserRole_statusForbidden() throws Exception {

		var requestBuilder = get("/task/management/system/executor/m.circle@example.com")
				.param("sortParams", "id,asc")
				.param("pageNum", "0")
				.param("pageSize", "5");
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void getAllTasksByExecutor_invalidRequestWithAdminRole_statusBadRequest() throws Exception {
		
		var requestBuilder = get("/task/management/system/executor/s.spiegel$example.com") //Not valid email
			.param("sortParam", "id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
				
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isBadRequest(),
				content().string(containsString("Invalid argument cause exception:"))
			);	
	}

	// GET /task/management/system/{id}/comments
	@Test 
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllComments_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments")
			.param("sortParams", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
			
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment2"))
			);
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void getAllComments_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments")
			.param("sortParam", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment2"))
			);
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	void getAllComments_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments")
			.param("sortParam", "id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment2"))
			);
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void getAllComments_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments")
			.param("sortParams", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isForbidden(),
				content().string("You do not have enough rights to get comments of task: 1")
		);
		
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	//GET /task/management/system/{id}/comments/{email}
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void getAllCommentsByAuthor_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception {

		var requestBuilder = get("/task/management/system/1/comments/b.baggins@example.com")
			.param("sortParams", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment3"))
			);
	}

	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void getAllCommentsByAuthor_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {

		var requestBuilder = get("/task/management/system/1/comments/b.baggins@example.com")
			.param("sortParams", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment3"))
			);
	}

	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	void getAllCommentsByAuthor_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments/b.baggins@example.com")
			.param("sortParam", "id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isOk(),
				jsonPath("$._embedded.taskCommentList[0].text", is("comment3"))
			);
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void getAllCommentsByAuthor_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = get("/task/management/system/1/comments/b.baggins@example.com")
			.param("sortParams", "task_comment_id,asc")
			.param("pageNum", "1")
			.param("pageSize", "1");
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isForbidden(),
				content().string("You do not have enough rights to get comments of task: 1")
		);
		
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	//POST /task/management/system/create
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	@Transactional
	void createNewTask_validRequest_statusCreated() throws Exception {
			
		var content =
				    """
					{
						"header": "header", 
						"description": "task", 
						"priority": "HIGH"
					}
					""";

		var requestBuilder = post("/task/management/system/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf());
		
		mvc.perform(requestBuilder) 
				.andExpectAll(
					status().isCreated(),
					header().string("location", "http://localhost:18080/task/management/system/6")
				);
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	void createNewTask_invalidRequest_statusBadRequest() throws Exception {

		var content = 
					"""
					{
						"headerRR": "header", 
						"description": "task", 
						"priority": "HIGH"
					}
					""";
		
		var requestBuilder = post("/task/management/system/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf());
		
		mvc.perform(requestBuilder)
				.andExpectAll(
					status().isBadRequest(),
					content().string(containsString("Header must be present"))
				);
	}

	//POST /task/management/system/{id}/comment
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	@Transactional
	void addComment_validRequestAuthorPerformingWithUserRole_statusCreated() throws Exception {
		
		var requestBuilder = post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());

		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isCreated(),
				header().string("location", matchesPattern("^http://localhost:18080/task/management/system/comments/([1-9][0-9]*)$"))
			);
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	@Transactional
	void addComment_validRequestExecutorPerformingWithUserRole_statusCreated() throws Exception {
		
		var requestBuilder = post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isCreated());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	@Transactional
	void addComment_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusCreated() throws Exception {
		
		
		var requestBuilder = post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isCreated());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com", role = "ROLE_USER")
	void addComment_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		
		var requestBuilder = post("/task/management/system/1/comment")
				.content("New comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isForbidden(),
				content().string("You do not have enough rights to add comments to task: 1")
			);
	}
	
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void addComment_invalidRequestAuthorPerformingWithUserRole_statusBadRequest() throws Exception {
		
		
		var requestBuilder = post("/task/management/system/1/comment")
				.contentType(MediaType.TEXT_PLAIN)
				.with(csrf());
		
		
		mvc.perform(requestBuilder)
			.andExpectAll(
					status().isBadRequest(), 
					content().string(containsString("Error deserializing request body: "))
			);
	}
	
	//PUT /task/management/system/{id}/status
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	@Transactional
	void updateStatus_validRequestNotAuthorOrExecutorPerformingWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = put("/task/management/system/2/status")
				.param("newStatus", "COMPLETED")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());

		mvc.perform(get("/task/management/system/2"))
			.andExpectAll(
				status().isOk(), 
				jsonPath("$.status", is("COMPLETED"))
			);
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	@Transactional
	void updateStatus_validRequestAuthorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = put("/task/management/system/2/status")
				.param("newStatus", "COMPLETED")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "m.circle", email = "m.circle@example.com")
	@Transactional
	void updateStatus_validRequestExecutorPerformingWithUserRole_statusOk() throws Exception {
		
		var requestBuilder = put("/task/management/system/2/status")
				.param("newStatus", "COMPLETED")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void updateStatus_validRequestNotAuthorOrExecutorPerformingWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = put("/task/management/system/2/status")
				.param("newStatus", "COMPLETED")
				.with(csrf());
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isForbidden(),
				content().string("You do not have enough rights to change the task: 2")
			);
	}
	
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	void updateStatus_invalidRequestWithAdminRole_statusBadRequest() throws Exception {
		
		var requestBuilder = put("/task/management/system/2/status")
				.param("newStaMtus", "COMPLETED")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpectAll(
				status().isBadRequest(), 
				content().string(containsString("Error due to missing required parameter"))
				);
	}
	
	//PUT /task/management/system/{id}/priority
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	@Transactional
	void updatePriority_validRequestWithAdminRole_statusOk() throws Exception {

		var requestBuilder = put("/task/management/system/2/priority")
				.param("newPriority", "LOW")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());

		mvc.perform(get("/task/management/system/2"))
			.andExpectAll(
				status().isOk(), 
				jsonPath("$.priority", is("LOW"))
			);
	}

	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void updatePriority_validRequestWithUserRole_statusForbidden() throws Exception {

		var requestBuilder = put("/task/management/system/2/priority")
				.param("newPriority", "LOW")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	void updatePriority_invalidRequestWithAdminRole_statusBadRequest() throws Exception {

		var requestBuilder = put("/task/management/system/2/priority")
				.param("newPrioRity", "LOW")
				.with(csrf());
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isBadRequest(),
				content().string(containsString("Error due to missing required parameter:"))
			);
	}

	//PUT /task/management/system/{id}/executor
	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	@Transactional
	void setExecutor_validRequestWithAdminRole_statusOk() throws Exception {

		var requestBuilder = put("/task/management/system/2/executor")
				.param("email", "b.baggins@example.com")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());

		mvc.perform(get("/task/management/system/2"))
			.andExpectAll(
				status().isOk(), 
				jsonPath("$.executor", is("b.baggins@example.com"))
			);
	}

	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com", role = "ROLE_USER")
	void setExecutor_validRequestWithUserRole_statusForbidden() throws Exception {

		var requestBuilder = put("/task/management/system/2/executor")
				.param("email", "b.baggins@example.com")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}

	@Test
	@WithCustomUser(username = "s.spiegel", email = "s.spiegel@example.com")
	void setExecutor_invalidRequestWithAdminRole_statusBadRequest() throws Exception {

		var requestBuilder = put("/task/management/system/2/executor")
				.param("email", "b.baggins$example.com")
				.with(csrf());
		
		mvc.perform(requestBuilder)
			.andExpectAll(
				status().isBadRequest(), 
				content().string(containsString("Invalid argument cause exception:"))
			);
	}

	//DELETE /task/management/system/{id}/delete
	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com")
	@Transactional
	void deleteCreatedTask_validRequestWithAdminRole_statusOk() throws Exception {
		
		var requestBuilder = delete("/task/management/system/3/delete") 
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isOk());

		mvc.perform(get("/task/management/system/3"))
			.andExpectAll(
				status().isBadRequest(),
				content().string("Can't find task with id: 3")
			);
	}

	@Test
	@WithCustomUser(username = "b.baggins", email = "b.baggins@example.com", role = "ROLE_USER")
	void deleteCreatedTask_validRequestWithUserRole_statusForbidden() throws Exception {
		
		var requestBuilder = delete("/task/management/system/3/delete")
				.with(csrf());
		
		mvc.perform(requestBuilder).andExpect(status().isForbidden());
	}
}
