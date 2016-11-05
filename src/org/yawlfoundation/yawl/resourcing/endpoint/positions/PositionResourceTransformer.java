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

import org.yawlfoundation.yawl.resourcing.endpoint.groups.OrgGroupResourceTransformer;
import org.yawlfoundation.yawl.resourcing.resource.Position;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Transforms the internal entities to resources.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
public final class PositionResourceTransformer {

	public static List<PositionResource> transformToResources(Collection<Position> entities) {
		List<PositionResource> resources = new LinkedList<PositionResource>();
		for(Position entity : entities) {
			resources.add(transformToResource(entity));
		}
		return resources;
	}


	public static List<String> transformToIds(Collection<Position> entities) {
        List<String> resources = new LinkedList<String>();
        for(Position entity : entities) {
            resources.add(entity.getID());
        }
        return resources;
    }


	public static List<String> transformToNames(Collection<Position> entities) {
        List<String> resources = new LinkedList<String>();
        for(Position entity : entities) {
            resources.add(entity.getName());
        }
        return resources;
    }


	public static PositionResource transformToResource(Position position) {
		PositionResource resource = new PositionResource();
		resource.setId(position.getID());
		resource.setName(position.getName());
		resource.setNotes(position.getNotes());
		resource.setDescription(position.getDescription());
		if(position.getOrgGroup() != null) {
			resource.setBelongsToOrgGroup(position.getOrgGroup().getID());
		}
		if(position.getReportsTo() != null) {
			resource.setReportsTo(position.getReportsTo().getID());
		}
		return resource;
	}

}
