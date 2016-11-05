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

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResource;

import java.util.LinkedList;
import java.util.List;


/**
 * The authentication provider that retrieves data from YAWL Resource Service.
 * @author Philipp R. Thomas <philipp.thomas@smail.inf.h-brs.de>
 */
public class YawlUserDetailsService implements UserDetailsService {

	private final UserRepository		userRepository;



	public YawlUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			UserResource user = userRepository.findByUsername(username);
			if(user == null) {
				throw new UsernameNotFoundException("User \""+username+"\" not found!");
			}

			String realPassword = userRepository.getPasswordById(user.getId());
			if(realPassword == null) {
				throw new AuthenticationServiceException("Password for user \""+username+"\" could not be retrieved!");
			}

			List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			if(user.getAdmin()) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}

			return new User(username, realPassword, authorities);
		}
		catch(UsernameNotFoundException e) {
			throw e;
		}
		catch(Exception e) {
			throw new AuthenticationServiceException("Error while retrieving user data!", e);
		}
	}


}
