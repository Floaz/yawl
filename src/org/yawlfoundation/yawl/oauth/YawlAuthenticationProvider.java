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

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResource;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.util.LinkedList;
import java.util.List;


/**
 * The authentication provider that retrieves data from YAWL Resource Service.
 * @author Philipp R. Thomas <philipp.thomas@smail.inf.h-brs.de>
 */
public class YawlAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;

	
	
	public YawlAuthenticationProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		try {
			String username = authentication.getName();
			String password = authentication.getCredentials().toString();
			password = PasswordEncryptor.encrypt(password, password);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if(!password.equals(userDetails.getPassword())) {
				throw new BadCredentialsException("Credentials are not correct!");
			}

			Authentication auth = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
			return auth;
		}
		catch(AuthenticationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new AuthenticationServiceException("Service error while checking credentials! " + e.getMessage(), e);
		}
	}


	@Override
	public boolean supports(Class<?> type) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);
	}

}
