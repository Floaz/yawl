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
package org.yawlfoundation.yawl.resourcing.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.resource.Participant;
import org.yawlfoundation.yawl.resourcing.rsInterface.ConnectionCache;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.util.LinkedList;


/**
 * The application configuration for security aspects of the stateless web api.
 *
 * @author Philipp Thomas
 * @date 21/11/2016
 */
public class AuthenticationProviderImpl implements AuthenticationProvider {

	private final ResourceManager		resourceManager;

	private final ConnectionCache		connectionCache;

	private final UserDetailsService	userDetailsService;

	
	
	public AuthenticationProviderImpl(ResourceManager resourceManager,
									  ConnectionCache connectionCache,
									  UserDetailsService userDetailsService) {
		this.resourceManager = resourceManager;
		this.connectionCache = connectionCache;
		this.userDetailsService = userDetailsService;
	}
	


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		// First try to login with external client users.
		String externalClientsPassword = connectionCache.getPassword(username);
		if(externalClientsPassword != null) {
			if(externalClientsPassword.equals(PasswordEncryptor.encrypt(password, password))) {
				LinkedList<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				Authentication auth = new UsernamePasswordAuthenticationToken(username, password, authorities);
				return auth;
			}
		}


		// Then try to login as normal user / participant.
		UserDetails user = userDetailsService.loadUserByUsername(username);
        Participant participant = resourceManager.getParticipantFromUserID(username);
        
        if(participant != null) {
            if(participant.getPassword().equals(PasswordEncryptor.encrypt(password, password))) {
    			Authentication auth = new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    			return auth;
            }
        }

		throw new BadCredentialsException("The credentials are not correct!");
	}


	@Override
	public boolean supports(Class<?> type) {
		return true;
		//return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);	
	}

}
