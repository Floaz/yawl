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
package org.yawlfoundation.yawl.resourcing.endpoint;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The service for checking authorities.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
public class AuthorizationCheckerImpl implements AuthorizationChecker {

	@Override
	public void checkForRole(String role) {
		if(!hasRole(role)) {
			throw new AccessDeniedException("Access denied!");
		}
	}

	
	@Override
	public void checkForAnyRole(String... roles) {
		if(!hasAnyRole(roles)) {
			throw new AccessDeniedException("Access denied!");
		}
	}

	
	@Override
	public boolean hasRole(String role) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for(GrantedAuthority authority : authorities) {
			if(authority.getAuthority().equals("ROLE_"+role)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean hasAnyRole(String... roles) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for(GrantedAuthority authority : authorities) {
			for(String role : roles) {
				if(authority.getAuthority().equals("ROLE_"+role)) {
					return true;
				}
			}
		}
		return false;
	}

}
