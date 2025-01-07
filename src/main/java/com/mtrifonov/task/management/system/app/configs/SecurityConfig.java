package com.mtrifonov.task.management.system.app.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
	
	@Value("${spring.security.oauth2.client.provider.keycloak.user-name-attribute}")
	private String userNameClaim;
	
	@Value("${spring.security.authorities-claim}")
	private String authoritiesClaim;
	
	@Value("${spring.security.role-prefix}")
	private String rolePrefix;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
		.oauth2Login(Customizer.withDefaults())
		.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
		.oauth2Client(Customizer.withDefaults())
		//.csrf(CsrfConfigurer::disable)
		
		.authorizeHttpRequests(a -> a
				.requestMatchers(
						AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/**/create"),
						AntPathRequestMatcher.antMatcher(HttpMethod.DELETE),
						AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/**/{id}/priority"),
						AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/**/{id}/executor"),
						AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**/author/{email}"),
						AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**/executor/{email}"))
				.hasRole("ADMIN")
				.anyRequest().authenticated())
		.build();
	}
	
	@Bean
	public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
		OidcUserService userService = new OidcUserService();
		
		return request -> {
			OidcUser user = userService.loadUser(request);
			List<GrantedAuthority> ath = user.getClaimAsStringList(authoritiesClaim)
					.stream()
					.filter(r -> r.startsWith(rolePrefix))
					.map(SimpleGrantedAuthority::new)
					.map(GrantedAuthority.class::cast)
					.toList();
			
			return new DefaultOidcUser(ath, user.getIdToken(), user.getUserInfo(), userNameClaim);
		};
	}
	
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setPrincipalClaimName(userNameClaim);
		converter.setJwtGrantedAuthoritiesConverter(request -> {
			return request.getClaimAsStringList(authoritiesClaim)
					.stream()
					.filter(r -> r.startsWith(rolePrefix))
					.map(SimpleGrantedAuthority::new)
					.map(GrantedAuthority.class::cast)
					.toList();
		});
		return converter;
	}
}
