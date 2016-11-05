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


import java.util.LinkedList;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.resource.Participant;



/**
 * The application configuration for security aspects of the stateless web api.
 *
 * @author Philipp Thomas
 * @date 21/11/2016
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	private ResourceManager resourceManager;


	public UserDetailsServiceImpl(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LinkedList<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();

		Participant participant = resourceManager.getParticipantFromUserID(username);
		if(participant == null) {
			throw new UsernameNotFoundException("User \""+username+"\" not found!");
		}

		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if(participant.isAdministrator()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		// The next 3 lines prevent login for normal users.
		if(!participant.isAdministrator()) {
			throw new LockedException("User \""+username+"\" found, but is not admin!");
		}
	
		return new User(participant.getUserID(), "", authorities);
	}

}
