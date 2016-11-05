/*
 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */
package org.yawlfoundation.yawl.resourcing.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationCheckerImpl;
import org.yawlfoundation.yawl.resourcing.security.AuthenticationProviderImpl;
import org.yawlfoundation.yawl.resourcing.security.UserDetailsServiceImpl;



/**
 * The application configuration for security aspects of the stateless web api.
 *
 * @author Philipp Thomas
 * @date 21/11/2016
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Value("${oauth.token.check.url:http://localhost:8080/auth/oauth/check_token}")
	private String oauthTokenCheckUrl;

	@Value("${oauth.client.id:yawl-resource-service}")
	private String oauthClientId;

	@Value("${oauth.client.secret:yawl-resource-service}")
	private String oauthClientSecret;

	@Autowired
	private ApplicationConfig applicationConfig;


	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(tokenServices());
	}


	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
//				.antMatchers("/api/workitems").access("#oauth2.hasScope('read')")
//				.antMatchers("/api/workitems/all").hasRole("ADMIN")
//				.antMatchers("/api/workitems/unoffered").hasRole("ADMIN")
//				.antMatchers("/api/workitems/worklisted").hasRole("ADMIN")
				.antMatchers("/api/**").authenticated()
				.anyRequest().permitAll()
				.and()
				.httpBasic();
	}

	@Bean
	public RemoteTokenServices tokenServices() {
		RemoteTokenServices tokenServices = new RemoteTokenServices();
		tokenServices.setClientId(oauthClientId);
		tokenServices.setClientSecret(oauthClientSecret);
		tokenServices.setCheckTokenEndpointUrl(oauthTokenCheckUrl);
		return tokenServices;
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(yawlAuthenticationProvider());
	}

	@Bean
	protected UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl(applicationConfig.resourceManager());
	}


	@Bean
	protected AuthenticationProviderImpl yawlAuthenticationProvider() {
		return new AuthenticationProviderImpl(applicationConfig.resourceManager(),
												applicationConfig.connectionCache(),
												userDetailsService());
	}


	@Bean
	public AuthorizationChecker authorizationChecker() {
		return new AuthorizationCheckerImpl();
	}

}
