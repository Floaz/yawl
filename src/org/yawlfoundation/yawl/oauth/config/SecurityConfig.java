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
package org.yawlfoundation.yawl.oauth.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.yawlfoundation.yawl.oauth.UserRepository;
import org.yawlfoundation.yawl.oauth.YawlAuthenticationProvider;
import org.yawlfoundation.yawl.oauth.YawlUserDetailsService;


/**
 * The configuration for security aspects of the authentication service.
 *
 * @author Philipp R. Thomas
 * @date 2016-12-15
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${resourceservice.url:http://localhost:8080/resourceService/}")
	private String resourceServiceUrl;

	@Value("${resourceservice.username:admin}")
	private String resourceServiceUsername;

	@Value("${resourceservice.password:YAWL}")
	private String resourceServicePassword;



	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(yawlAuthenticationProvider());
	}

	@Override
	public void configure(WebSecurity http) throws Exception {
		http.ignoring().antMatchers("/webjars/**", "/images/**", "/oauth/uncache_approvals", "/oauth/cache_approvals");
    }

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsServiceBean() {
		return new YawlUserDetailsService(userRepository());
	}

	@Bean
	public UserRepository userRepository() {
		return new UserRepository(resourceServiceUrl, resourceServiceUsername, resourceServicePassword);
	}

	@Bean
	public YawlAuthenticationProvider yawlAuthenticationProvider() {
		return new YawlAuthenticationProvider(userDetailsServiceBean());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
				.anyRequest().permitAll()
				.and()
				.httpBasic();
	}
}
