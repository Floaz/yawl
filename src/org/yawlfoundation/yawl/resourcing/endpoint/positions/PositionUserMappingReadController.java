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
package org.yawlfoundation.yawl.resourcing.endpoint.positions;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserIdentifiersListResource;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResourceTransformer;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UsersListResource;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;
import org.yawlfoundation.yawl.resourcing.resource.Position;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading positions users mappings.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class PositionUserMappingReadController {

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

    @RequestMapping(value="/position/{id}/users",
                    method=RequestMethod.GET)
    public UsersListResource getUsersByPositionId(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

        Set<Participant> participants = getOrgDataSet().getPositionParticipants(id);

        if(participants == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        return new UsersListResource(UserResourceTransformer.transformToResources(participants));
    }


    @RequestMapping(value="/position/{id}/users/identifiers",
            method=RequestMethod.GET)
    public UserIdentifiersListResource getUserIdsByPositionId(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

        Set<Participant> participants = getOrgDataSet().getPositionParticipants(id);

        if(participants == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        return new UserIdentifiersListResource(UserResourceTransformer.transformToIds(participants));
    }


    @RequestMapping(value="/position/by-name/{name}/users",
            method=RequestMethod.GET)
    public UsersListResource getUsersByPositionName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPositionByLabel(name);

        if(position == null) {
            throw new NotFoundException("No such positions with this name!");
        }

        Set<Participant> participants = getOrgDataSet().getPositionParticipants(position.getID());

        return new UsersListResource(UserResourceTransformer.transformToResources(participants));
    }


    @RequestMapping(value="/position/by-name/{name}/users/identifiers",
            method=RequestMethod.GET)
    public UserIdentifiersListResource getUserIdsByPositionName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPositionByLabel(name);

        if(position == null) {
            throw new NotFoundException("No such positions with this name!");
        }

        Set<Participant> participants = getOrgDataSet().getPositionParticipants(position.getID());

        return new UserIdentifiersListResource(UserResourceTransformer.transformToIds(participants));
    }


    @RequestMapping(value="/user/{id}/positions",
            method=RequestMethod.GET)
    public PositionsListResource getPositionsByUserId(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        Set<Position> positions = getOrgDataSet().getParticipantPositions(id);

        if(positions == null) {
            throw new NotFoundException("No such participant with this id!");
        }

        List<PositionResource> resources = PositionResourceTransformer.transformToResources(positions);
        return new PositionsListResource(resources);
    }


    @RequestMapping(value="/user/by-username/{uid}/positions",
            method=RequestMethod.GET)
    public PositionsListResource getPositionsByUserName(@PathVariable("uid") String uid) {
        authorizationChecker.checkForRole("ADMIN");

        Participant participant = getOrgDataSet().getParticipantFromUserID(uid);

        if(participant == null) {
            throw new NotFoundException("No such participant with this users id!");
        }

        Set<Position> positions = getOrgDataSet().getParticipantPositions(participant.getID());
        List<PositionResource> resources = PositionResourceTransformer.transformToResources(positions);
        return new PositionsListResource(resources);
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
