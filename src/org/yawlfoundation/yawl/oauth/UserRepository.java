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
package org.yawlfoundation.yawl.oauth;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResource;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * The authentication provider that retrieves data from YAWL Resource Service.
 * @author Philipp R. Thomas <philipp.thomas@smail.inf.h-brs.de>
 */
public class UserRepository {

	private final RestTemplate 	restTemplate;

	private final String 		resourceServiceUrl;



	public UserRepository(String resourceServiceUrl, String username, String password) {
		restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = new LinkedList<ClientHttpRequestInterceptor>();
		interceptors.add(new BasicAuthorizationInterceptor(username, password));
		restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));

		this.resourceServiceUrl = resourceServiceUrl.endsWith("/") ? resourceServiceUrl : resourceServiceUrl + "/";
	}
	

	public UserResource findById(String id) {
		try {
			return restTemplate.getForObject(resourceServiceUrl + "api/user/{id}", UserResource.class, id);
		}
		catch (HttpClientErrorException ex)   {
			if (ex.getStatusCode().value() != 404) {
				throw ex;
			}
		}
		return null;
	}


	public UserResource findByUsername(String username) {
		try {
			return restTemplate.getForObject(resourceServiceUrl + "api/user/by-username/{username}", UserResource.class, username);
		}
		catch (HttpClientErrorException ex)   {
			if (ex.getStatusCode().value() != 404) {
				throw ex;
			}
		}
		return null;
	}


	public String getPasswordById(String id) {
		try {
			return restTemplate.getForObject(resourceServiceUrl + "api/user/{id}/password", String.class, id);
		}
		catch (HttpClientErrorException ex)   {
			if (ex.getStatusCode().value() != 404) {
				throw ex;
			}
		}
		return null;
	}

}
