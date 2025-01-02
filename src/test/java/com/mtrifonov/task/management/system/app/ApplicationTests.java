package com.mtrifonov.task.management.system.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.invoke.VarHandle;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClient;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
//@ContextConfiguration(classes = com.mtrifonov.task.management.system.app.)
@AutoConfigureMockMvc
@Slf4j
class ApplicationTests {
	
	@Autowired
	MockMvc mvc;
	private String location;
	@Test
	void contextLoads() {
	}
	
	@Test
	void createNewTask_validRequest_statusCreated() throws Exception {
			
		var requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/create")
				.content("{\"header\": \"header\", \"description\": \"task\", \"priority\": \"HIGH\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(oidcLogin()
						.idToken(token -> token.claim("email", "b.baggins@example.com"))
						.authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
						);
		
		var result = mvc.perform(requestBuilder)
				.andExpect(status().isCreated())
				.andReturn();
				
		requestBuilder = MockMvcRequestBuilders
				.post("/task/management/system/create")
				.content("{\"headerRR\": \"header\", \"description\": \"task\", \"priority\": \"HIGH\"}")
				.with(oidcLogin()
						.idToken(token -> token.claim("email", "b.baggins@example.com"))
						.authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
						);
		
		result = mvc.perform(requestBuilder)
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertTrue(result.getResponse().getContentAsString().contains("Header must be present"));
		
		requestBuilder = MockMvcRequestBuilders
				.get("/task/management/system/" + location.charAt(location.length() - 1))
				.with(oidcLogin()
						.idToken(token -> token.claim("email", "b.baggins@example.com"))
						.authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
						);
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
		
		requestBuilder = MockMvcRequestBuilders
				.delete("/task/management/system/delete/" + location.charAt(location.length() - 1))
				.with(oidcLogin()
						.idToken(token -> token.claim("email", "s.spiegel@example.com"))
						.authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
						);
		
		mvc.perform(requestBuilder).andExpect(status().isOk());
		
	}
}
