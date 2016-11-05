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

import org.yawlfoundation.yawl.resourcing.resource.Participant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Transforms the internal entities to resources.
 *
 * @author Philipp Thomas
 * @date 26/11/2016
 */
public final class UserResourceTransformer {

	public static List<UserResource> transformToResources(Collection<Participant> entities) {
		List<UserResource> resources = new LinkedList<UserResource>();
		for(Participant entity : entities) {
			resources.add(transformToResource(entity));
		}
		return resources;
	}


	public static List<String> transformToIds(Collection<Participant> entities) {
        List<String> resources = new LinkedList<String>();
        for(Participant entity : entities) {
            resources.add(entity.getID());
        }
        return resources;
    }


	public static List<String> transformToUsernames(Collection<Participant> entities) {
        List<String> resources = new LinkedList<String>();
        for(Participant entity : entities) {
            resources.add(entity.getUserID());
        }
        return resources;
    }


	public static List<String> transformToFullNames(Collection<Participant> entities) {
		List<String> resources = new LinkedList<String>();
		for(Participant entity : entities) {
			resources.add(entity.getFullName());
		}
		return resources;
	}


	public static UserResource transformToResource(Participant entity) {
		UserResource resource = new UserResource();
		resource.setId(entity.getID());
		resource.setUsername(entity.getUserID());
		resource.setFirstname(entity.getFirstName());
		resource.setLastname(entity.getLastName());
		resource.setNotes(entity.getNotes());
		resource.setDescription(entity.getDescription());
		resource.setAdmin(entity.isAdministrator());
		return resource;
	}

}
