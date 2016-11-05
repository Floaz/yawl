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
package org.yawlfoundation.yawl.resourcing.endpoint.workitems;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * The controller for Work Queues and Work Items.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
@RestController
public class WorkitemWriteController {

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

	@RequestMapping(value="/workitem/{id}/documentation",
					method=RequestMethod.PUT)
	public void updateDocumentation(@PathVariable("id") String id, @RequestBody EditDocumentationWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		if(request.getDocumentation() == null || request.getDocumentation().isEmpty()) {
			record.setDocumentation("");
			record.setDocumentationChanged(true);
		} else {
			record.setDocumentation(request.getDocumentation().trim());
			record.setDocumentationChanged(true);
		}

		resourceManager.getWorkItemCache().update(record);
	}


	@RequestMapping(value="/workitem/{id}/offer",
			method=RequestMethod.POST)
	public void offerWorkItemToUser(@PathVariable("id") String id, @RequestBody OfferWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		List<String> users = request.getUsers();

		for(String user : users) {
			Participant participant = resourceManager.getParticipantFromID(user);

			if (participant == null) {
				throw new ConflictException("No such participant with this id " + user);
			}
		}

		if(users.isEmpty()) {
			resourceManager.removeFromAll(record);
			resourceManager.getAdminQueues().getQueue(WorkQueue.UNOFFERED).add(record, false);
			record.setResourceStatus(WorkItemRecord.statusResourceUnoffered);
			resourceManager.getWorkItemCache().update(record);
		} else {
			resourceManager.reassignWorklistedItem(record, users.toArray(new String[]{}), "Reoffer");
		}
	}


	@RequestMapping(value="/workitem/{id}/allocate",
					method=RequestMethod.POST)
	public void allocateWorkItemToUser(@PathVariable("id") String id, @RequestBody AssignWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		Participant participant = resourceManager.getParticipantFromID(request.getUser());

		if(participant == null) {
			throw new ConflictException("No such participant with this id");
		}

		resourceManager.reassignWorklistedItem(record, new String[]{participant.getID()}, "Reallocate");
	}


	@RequestMapping(value="/workitem/{id}/start",
					method=RequestMethod.POST)
	public void startWorkItem(@PathVariable("id") String id, @RequestBody StartWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		Participant participant = resourceManager.getParticipantFromID(request.getUser());

		if(participant == null) {
			throw new ConflictException("No such participant with this id");
		}

		resourceManager.reassignWorklistedItem(record, new String[]{participant.getID()}, "Restart");
	}


	@RequestMapping(value="/workitem/{id}/suspend",
					method=RequestMethod.POST)
	public void suspendWorkItem(@PathVariable("id") String id, @RequestBody SuspendWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		Participant participant = resourceManager.getParticipantFromID(request.getUser());

		if(participant == null) {
			throw new ConflictException("No such participant with this id");
		}

		resourceManager.suspendWorkItem(participant, record);
	}


	@RequestMapping(value="/workitem/{id}/unsuspend",
			method=RequestMethod.POST)
	public void unsuspendWorkItem(@PathVariable("id") String id, @RequestBody UnsuspendWorkItemRequest request) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		Participant participant = resourceManager.getParticipantFromID(request.getUser());

		if(participant == null) {
			throw new ConflictException("No such participant with this id");
		}

		resourceManager.unsuspendWorkItem(participant, record);
	}





	public class ConflictException extends RuntimeException {
		public ConflictException(String message) {
			super(message);
		}
	}


	@ExceptionHandler(value = { ConflictException.class })
	@ResponseStatus (HttpStatus.CONFLICT)
	protected ErrorDetail handleConflictException(ConflictException ex) {
		return new ErrorDetail(HttpStatus.CONFLICT.value(), ex.getMessage());
	}

}
