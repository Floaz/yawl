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

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Transforms the internal entities to resources.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
public final class WorkitemResourceTransformer {

	public static List<WorkItemResource> transformToResources(Collection<WorkItemRecord> entities) {
		List<WorkItemResource> resources = new LinkedList<WorkItemResource>();
		for(WorkItemRecord entity : entities) {
			resources.add(transformToResource(entity));
		}
		return resources;
	}


	public static List<String> transformToIds(Collection<WorkItemRecord> entities) {
        List<String> resources = new LinkedList<String>();
        for(WorkItemRecord entity : entities) {
            resources.add(entity.getID());
        }
        return resources;
    }


	public static WorkItemResource transformToResource(WorkItemRecord record) {
		WorkItemResource resource = new WorkItemResource();
		resource.setId(record.getID());
		resource.setUniqueId(record.getUniqueID());
		resource.setSpecification(new WorkItemResource.Specification(record.getSpecIdentifier(), record.getSpecVersion(), record.getSpecURI()));
		resource.setCaseId(record.getCaseID());
		resource.setTask(new WorkItemResource.Task(record.getTaskID(), record.getTaskName()));
		resource.setDocumentation(record.getDocumentation());
		resource.setStatus(record.getStatus());
		resource.setStartedBy(record.getStartedBy());
		resource.setCompletedBy(record.getCompletedBy());
		resource.setEnablementTime(record.getEnablementTimeMs());
		resource.setFiringTime(record.getFiringTimeMs());
		resource.setStartTime(record.getStartTimeMs());
		resource.setCompletionTime(record.getCompletionTimeMs());
		resource.setTimerExpiry(record.getTimerExpiry());
		resource.setResourceStatus(record.getResourceStatus());
		return resource;
	}

}
