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
package org.yawlfoundation.yawl.resourcing.endpoint.roles;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Role;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading roles.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class RolesReadController {

	/**
	 ** Services ************************************************************
	 **/

	@Resource
    private ResourceManager resourceManager;
	
	@Resource
    private AuthorizationChecker authorizationChecker;


	/**
	 ** Setters for injection ***********************************************
	 **/

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}


	public void setAuthorizationChecker(AuthorizationChecker authorizationChecker) {
		this.authorizationChecker = authorizationChecker;
	}



	/**
	 ** Handlers *************************************************************
	 **/

	@RequestMapping(value={"/role", "/roles"},
					method=RequestMethod.GET)
	public RolesListResource getAllRoles() {
		authorizationChecker.checkForRole("ADMIN");

		Set<Role> roles = getOrgDataSet().getRoles();
		List<RoleResource> roleResources = RoleResourceTransformer.transformToResources(roles);
		return new RolesListResource(roleResources);
	}


    @RequestMapping(value={"/roles/identifiers"},
            method=RequestMethod.GET)
    public RoleIdentifiersListResource getAllRoleIds() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Role> roles = getOrgDataSet().getRoles();
        List<String> roleIds = RoleResourceTransformer.transformToIds(roles);
        return new RoleIdentifiersListResource(roleIds);
    }


    @RequestMapping(value={"/roles/names"},
            method=RequestMethod.GET)
    public RoleNamesListResource getAllRoleNames() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Role> roles = getOrgDataSet().getRoles();
        List<String> roleIds = RoleResourceTransformer.transformToNames(roles);
        return new RoleNamesListResource(roleIds);
    }


	@RequestMapping(value="/role/{id}",
			method=RequestMethod.GET)
	public RoleResource getRoleById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Role role = getOrgDataSet().getRole(id);

		if(role == null) {
			throw new NotFoundException("No such role with this id!");
		}

		return RoleResourceTransformer.transformToResource(role);
	}


	@RequestMapping(value="/role/by-name/{name}",
			method=RequestMethod.GET)
	public RoleResource getRoleByName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

		Role role = getOrgDataSet().getRoleByName(name);

		if(role == null) {
			throw new NotFoundException("No such role with this name!");
		}

		return RoleResourceTransformer.transformToResource(role);
	}



	protected ResourceDataSet getOrgDataSet() {
		while(resourceManager.isOrgDataRefreshing()) {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException ie) {
				// deliberately do nothing
			}
		}

		return resourceManager.getOrgDataSet();
	}

}
