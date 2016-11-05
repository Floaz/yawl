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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.resourcing.QueueSet;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UserResourceTransformer;
import org.yawlfoundation.yawl.resourcing.endpoint.users.UsersListResource;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * The controller for Work Queues and Work Items.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
@RestController
public class WorkitemReadController {

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

	@RequestMapping(value={"/workitems/all", "/workitems"},
					method=RequestMethod.GET)
	public WorkItemsListResource getAllWorkItems() {
		authorizationChecker.checkForRole("ADMIN");

		List<WorkItemRecord> workItems = new LinkedList<WorkItemRecord>();
		workItems.addAll(resourceManager.getAdminQueues().getQueuedWorkItems(WorkQueue.WORKLISTED));
		workItems.addAll(resourceManager.getAdminQueues().getQueuedWorkItems(WorkQueue.UNOFFERED));

		List<WorkItemResource> workItemResources = WorkitemResourceTransformer.transformToResources(workItems);
		return new WorkItemsListResource(workItemResources);
	}


	@RequestMapping(value={"/workitems/unoffered"},
					method=RequestMethod.GET)
	public WorkItemsListResource getAllUnofferedWorkItems() {
		authorizationChecker.checkForRole("ADMIN");

		List<WorkItemRecord> workItems = new LinkedList<WorkItemRecord>();
		workItems.addAll(resourceManager.getAdminQueues().getQueuedWorkItems(WorkQueue.UNOFFERED));

		List<WorkItemResource> workItemResources = WorkitemResourceTransformer.transformToResources(workItems);
		return new WorkItemsListResource(workItemResources);
	}


	@RequestMapping(value={"/workitems/worklisted"},
					method=RequestMethod.GET)
	public WorkItemsListResource getAllWorklistedWorkItems() {
		authorizationChecker.checkForRole("ADMIN");

		List<WorkItemRecord> workItems = new LinkedList<WorkItemRecord>();
		workItems.addAll(resourceManager.getAdminQueues().getQueuedWorkItems(WorkQueue.WORKLISTED));

		List<WorkItemResource> workItemResources = WorkitemResourceTransformer.transformToResources(workItems);
		return new WorkItemsListResource(workItemResources);
	}


	@RequestMapping(value={"/user/{pid}/workitems/{queue}"},
					method=RequestMethod.GET)
	public WorkItemsListResource getOfferedWorkItemsOfParticipant(@PathVariable("pid") String pid, @PathVariable("queue") String queue) {
		authorizationChecker.checkForRole("ADMIN");

		Participant participant = resourceManager.getParticipantFromID(pid);
		if(participant == null) {
			throw new NotFoundException("No such participant with this id");
		}

		return getOfferedWorkItemsOfParticipantByUsername(participant.getUserID(), queue);
	}


	@RequestMapping(value={"/user/by-uid/{userId}/workitems/{queue}"},
					method=RequestMethod.GET)
	public WorkItemsListResource getOfferedWorkItemsOfParticipantByUsername(@PathVariable("userId") String userId,
																			@PathVariable("queue") String queue) {
		authorizationChecker.checkForRole("ADMIN");

		List<WorkItemRecord> workItems = new LinkedList<WorkItemRecord>();

		int queueValue = 0;
		if(queue.equals("offered")) {
			queueValue = WorkQueue.OFFERED;
		}
		else if(queue.equals("allocated")) {
			queueValue = WorkQueue.ALLOCATED;
		}
		else if(queue.equals("started")) {
			queueValue = WorkQueue.STARTED;
		}
		else if(queue.equals("suspended")) {
			queueValue = WorkQueue.SUSPENDED;
		}
		else {
			throw new NotFoundException("No such queue with this name exists");
		}

		QueueSet queueSet = resourceManager.getUserQueueSet(userId);
		if(queueSet == null) {
			throw new NotFoundException("No such participant with this id");
		}

		workItems.addAll(queueSet.getQueuedWorkItems(queueValue));

		List<WorkItemResource> workItemResources = WorkitemResourceTransformer.transformToResources(workItems);
		return new WorkItemsListResource(workItemResources);
	}


	@RequestMapping(value="/workitem/{id}",
					method=RequestMethod.GET)
	public WorkItemResource getWorkItemById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		return WorkitemResourceTransformer.transformToResource(record);
	}


	@RequestMapping(value="/workitem/{id}/participants",
					method=RequestMethod.GET)
	public UsersListResource getParticipantOfWorkItem(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		WorkItemRecord record = resourceManager.getWorkItemRecord(id);

		if(record == null) {
			throw new NotFoundException("No such work item with this id");
		}

		Set<Participant> participants = resourceManager.getParticipantsAssignedWorkItem(record) ;
		if(participants == null) {
			participants = new HashSet<Participant>();
		}

		return new UsersListResource(UserResourceTransformer.transformToResources(participants));
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
