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

import org.yawlfoundation.yawl.resourcing.resource.Role;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Transforms the internal entities to resources.
 *
 * @author Philipp Thomas
 * @date 26/11/2016
 */
public final class RoleResourceTransformer {

	public static List<RoleResource> transformToResources(Collection<Role> entities) {
		List<RoleResource> resources = new LinkedList<RoleResource>();
		for(Role entity : entities) {
			resources.add(transformToResource(entity));
		}
		return resources;
	}


	public static List<String> transformToIds(Collection<Role> entities) {
        List<String> resources = new LinkedList<String>();
        for(Role entity : entities) {
            resources.add(entity.getID());
        }
        return resources;
    }


	public static List<String> transformToNames(Collection<Role> entities) {
        List<String> resources = new LinkedList<String>();
        for(Role entity : entities) {
            resources.add(entity.getName());
        }
        return resources;
    }


	public static RoleResource transformToResource(Role entity) {
		RoleResource resource = new RoleResource();
		resource.setId(entity.getID());
		resource.setName(entity.getName());
		resource.setNotes(entity.getNotes());
		resource.setDescription(entity.getDescription());
		if(entity.getOwnerRole() != null) {
			resource.setBelongsTo(entity.getOwnerRole().getID());
		}
		return resource;
	}

}
