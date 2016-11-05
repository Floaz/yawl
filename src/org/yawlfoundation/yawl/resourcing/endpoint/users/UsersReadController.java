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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading users.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class UsersReadController {

	/**
	 ** Services ************************************************************
	 **/

	@Resource
    private ResourceManager 		resourceManager;
	
	@Resource
    private AuthorizationChecker 	authorizationChecker;


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

	@RequestMapping(value={"/users", "/users"},
					method=RequestMethod.GET)
	public UsersListResource getAllUsers() {
		authorizationChecker.checkForRole("ADMIN");

		Set<Participant> users = getOrgDataSet().getParticipants();
		List<UserResource> resources = UserResourceTransformer.transformToResources(users);
		return new UsersListResource(resources);
	}


    @RequestMapping(value={"/users/identifiers"},
            method=RequestMethod.GET)
    public UserIdentifiersListResource getAllUserIds() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Participant> users = getOrgDataSet().getParticipants();
        List<String> ids = UserResourceTransformer.transformToIds(users);
        return new UserIdentifiersListResource(ids);
    }


    @RequestMapping(value={"/users/usernames"},
            method=RequestMethod.GET)
    public UserNamesListResource getAllUserUsernames() {
        authorizationChecker.checkForRole("ADMIN");

        Set<Participant> users = getOrgDataSet().getParticipants();
        List<String> usernames = UserResourceTransformer.transformToUsernames(users);
        return new UserNamesListResource(usernames);
    }


    @RequestMapping(value={"/users/fullnames"},
            method=RequestMethod.GET)
    public UserNamesListResource getAllUserFullNames() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Participant> users = getOrgDataSet().getParticipants();
        List<String> names = UserResourceTransformer.transformToFullNames(users);
        return new UserNamesListResource(names);
    }


	@RequestMapping(value={"/user/{id}", "/user/by-id/{uid}"},
			method=RequestMethod.GET)
	public UserResource getUserById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Participant user = getOrgDataSet().getParticipant(id);

		if(user == null) {
			throw new NotFoundException("No such users with this id!");
		}

		return UserResourceTransformer.transformToResource(user);
	}


	@RequestMapping(value={"/user/by-uid/{uid}", "/user/by-username/{uid}"},
			method=RequestMethod.GET)
	public UserResource getUserByUsername(@PathVariable("uid") String uid) {
		authorizationChecker.checkForRole("ADMIN");

        Participant user = getOrgDataSet().getParticipantFromUserID(uid);

        if(user == null) {
            throw new NotFoundException("No such users with this id!");
        }

        return UserResourceTransformer.transformToResource(user);
	}


	@RequestMapping(value={"/user/{id}/password", "/user/by-id/{id}/password"},
					method=RequestMethod.GET)
	public String getPasswordById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Participant user = getOrgDataSet().getParticipant(id);

		if(user == null) {
			throw new NotFoundException("No user with this id!");
		}

		return user.getPassword();
	}


	@RequestMapping(value={"/user/by-uid/{uid}/password", "/user/by-username/{uid}/password"},
					method=RequestMethod.GET)
	public String getPasswordByUsername(@PathVariable("uid") String uid) {
		authorizationChecker.checkForRole("ADMIN");

		Participant user = getOrgDataSet().getParticipantFromUserID(uid);

		if(user == null) {
			throw new NotFoundException("No user with this id!");
		}

		return user.getPassword();
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
