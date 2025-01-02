package com.mtrifonov.task.management.system.app.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomUser annotation) {
		
		var jwt = Jwt
				.withTokenValue("token")
				.header("alg", "RS256")
				.header("typ", "JWT")
				.claim("preferred_username", annotation.username())
				.claim("email", annotation.email())
				.build();
		
		var token = new JwtAuthenticationToken(jwt, 
				List.of(new SimpleGrantedAuthority(annotation.role())),
				annotation.username());
		
		SecurityContext ctx = SecurityContextHolder.createEmptyContext();
		ctx.setAuthentication(token);
		return ctx;
	}

}
