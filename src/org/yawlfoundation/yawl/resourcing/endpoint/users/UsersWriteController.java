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
package org.yawlfoundation.yawl.resourcing.endpoint.users;

import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.BadRequestException;
import org.yawlfoundation.yawl.resourcing.exception.ConflictException;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;

import javax.annotation.Resource;


/**
 * The controller for editing users.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class UsersWriteController {

	/**
	 ** Services ************************************************************
	 **/

	@Resource
    private ResourceManager         resourceManager;
	
	@Resource
    private AuthorizationChecker    authorizationChecker;


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

	@RequestMapping(value={"/user", "/users"},
					method=RequestMethod.POST)
	public UserResource addNewUser(@RequestBody NewUserRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Participant)) {
            throw new RuntimeException("Users are not editable!");
        }

        Participant found = resourceManager.getOrgDataSet().getParticipantFromUserID(resource.getUsername());

        if(found != null) {
            throw new ConflictException("User with this username already exists!");
        }

		Participant user = new Participant();
        user.setUserID(resource.getUsername());
        user.setFirstName(resource.getFirstname());
        user.setLastName(resource.getLastname());
        user.setPassword(resource.getPassword(), true);
        user.setDescription(resource.getDescription());
        user.setNotes(resource.getNotes());

        String newId = resourceManager.addParticipant(user);

        if(newId.startsWith("<fail")) {
            throw new RuntimeException("Error while adding users: "+newId);
        }

        return UserResourceTransformer.transformToResource(user);
	}


	@RequestMapping(value="/user/{id}",
			        method=RequestMethod.PUT)
	public void editUser(@PathVariable("id") String id,
                            @RequestBody EditUserRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Participant)) {
            throw new RuntimeException("Users are not editable!");
        }

		Participant user = resourceManager.getParticipantFromID(id);

		if(user == null) {
			throw new NotFoundException("No such user with this id!");
		}

        if(resource.getUsername() != null) {
            user.setUserID(resource.getUsername());
        }

        if(resource.getPassword() != null) {
            user.setPassword(resource.getPassword(), true);
        }

		if(resource.getLastname() != null) {
            user.setLastName(resource.getLastname());
        }

        if(resource.getFirstname() != null) {
            user.setFirstName(resource.getFirstname());
        }

        if(resource.getNotes() != null) {
            user.setNotes(resource.getNotes());
        }

        if(resource.getDescription() != null) {
            user.setDescription(resource.getDescription());
        }

        resourceManager.updateParticipant(user);
	}


    @RequestMapping(value="/user/{id}/password",
            method=RequestMethod.PUT)
    public void changePasswordOfUser(@PathVariable("id") String id,
                                     @RequestBody String newPassword) {
        authorizationChecker.checkForRole("ADMIN");

        if(newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException("Password not valid!");
        }

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Participant)) {
            throw new RuntimeException("Users are not editable!");
        }

        Participant user = resourceManager.getParticipantFromID(id);

        if(user == null) {
            throw new NotFoundException("No such user with this id!");
        }

        user.setPassword(newPassword, true);

        resourceManager.updateParticipant(user);
    }


    @RequestMapping(value="/user/{id}",
                    method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Participant)) {
            throw new RuntimeException("Participants are not editable!");
        }

        boolean result = resourceManager.removeParticipant(id);

        if(!result) {
            throw new NotFoundException("No such users with this id!");
        }
    }

}
