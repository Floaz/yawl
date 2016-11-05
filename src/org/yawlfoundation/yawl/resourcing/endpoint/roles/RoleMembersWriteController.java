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

import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResource;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;
import org.yawlfoundation.yawl.resourcing.resource.Role;

import javax.annotation.Resource;


/**
 * The controller for managing role users mappings.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class RoleMembersWriteController {

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


    @RequestMapping(value="/user/{userId}/roles",
                    method=RequestMethod.POST)
    public void addRoleToUser(@PathVariable("userId") String participantId, @RequestBody RoleResource roleResource) {
        authorizationChecker.checkForRole("ADMIN");

        Role role = getOrgDataSet().getRole(roleResource.getId());

        if(role == null) {
            throw new NotFoundException("No such role with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addRole(role);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/user/{userId}/roles/identifiers",
            method=RequestMethod.POST)
    public void addRoleToUser(@PathVariable("userId") String participantId, @RequestBody String roleId) {
        authorizationChecker.checkForRole("ADMIN");

        Role role = getOrgDataSet().getRole(roleId);

        if(role == null) {
            throw new NotFoundException("No such role with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addRole(role);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/role/{id}/users",
                    method=RequestMethod.POST)
    public void addUserToRole(@PathVariable("id") String id,
                              @RequestBody UserResource user) {
		authorizationChecker.checkForRole("ADMIN");

        Role role = getOrgDataSet().getRole(id);

        if(role == null) {
            throw new NotFoundException("No such role with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(user.getId());

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addRole(role);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/role/{id}/users/identifiers",
                    method=RequestMethod.POST)
    public void addUserToRole(@PathVariable("id") String id, @RequestBody String participantId) {
		authorizationChecker.checkForRole("ADMIN");

        Role role = getOrgDataSet().getRole(id);

        if(role == null) {
            throw new NotFoundException("No such role with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addRole(role);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value={"/role/{roleId}/user/{userId}", "/user/{userId}/role/{roleId}"},
                    method=RequestMethod.DELETE)
    public void deleteUserFromRole(@PathVariable("roleId") String id, @PathVariable("userId") String participantId) {
        authorizationChecker.checkForRole("ADMIN");

        Role role = getOrgDataSet().getRole(id);

        if(role == null) {
            throw new NotFoundException("No such role with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.removeRole(role);

        resourceManager.updateParticipant(participant);
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
