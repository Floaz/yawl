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

import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResource;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;
import org.yawlfoundation.yawl.resourcing.resource.Position;

import javax.annotation.Resource;


/**
 * The controller for managing positions users mappings.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class PositionUserMappingWriteController {

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


    @RequestMapping(value="/user/{userId}/positions",
                    method=RequestMethod.POST)
    public void addPositionToUser(@PathVariable("userId") String participantId, @RequestBody PositionResource positionResource) {
        authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPosition(positionResource.getId());

        if(position == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addPosition(position);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/user/{userId}/positions/identifiers",
            method=RequestMethod.POST)
    public void addPositionToUser(@PathVariable("userId") String participantId, @RequestBody String positionId) {
        authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPosition(positionId);

        if(position == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addPosition(position);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/position/{id}/users",
                    method=RequestMethod.POST)
    public void addUserToPosition(@PathVariable("id") String id,
                              @RequestBody UserResource user) {
		authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPosition(id);

        if(position == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(user.getId());

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addPosition(position);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value="/position/{id}/users/identifiers",
                    method=RequestMethod.POST)
    public void addUserToPosition(@PathVariable("id") String id, @RequestBody String participantId) {
		authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPosition(id);

        if(position == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.addPosition(position);

        resourceManager.updateParticipant(participant);
    }


    @RequestMapping(value={"/position/{positionId}/user/{userId}", "/user/{userId}/position/{positionId}"},
                    method=RequestMethod.DELETE)
    public void deleteUserFromPosition(@PathVariable("positionId") String id, @PathVariable("userId") String participantId) {
        authorizationChecker.checkForRole("ADMIN");

        Position position = getOrgDataSet().getPosition(id);

        if(position == null) {
            throw new NotFoundException("No such positions with this id!");
        }

        Participant participant = getOrgDataSet().getParticipant(participantId);

        if(participant == null) {
            throw new NotFoundException("No such users with this id!");
        }

        participant.removePosition(position);

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
