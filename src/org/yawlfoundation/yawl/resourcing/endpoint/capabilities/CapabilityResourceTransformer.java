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
package org.yawlfoundation.yawl.resourcing.endpoint.capabilities;

import org.yawlfoundation.yawl.resourcing.resource.Capability;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Transforms the internal entities to resources.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
public final class CapabilityResourceTransformer {

	public static List<CapabilityResource> transformToResources(Collection<Capability> entities) {
		List<CapabilityResource> resources = new LinkedList<CapabilityResource>();
		for(Capability entity : entities) {
			resources.add(transformToResource(entity));
		}
		return resources;
	}


	public static List<String> transformToIds(Collection<Capability> entities) {
        List<String> resources = new LinkedList<String>();
        for(Capability entity : entities) {
            resources.add(entity.getID());
        }
        return resources;
    }


	public static List<String> transformToNames(Collection<Capability> entities) {
        List<String> resources = new LinkedList<String>();
        for(Capability entity : entities) {
            resources.add(entity.getName());
        }
        return resources;
    }


	public static CapabilityResource transformToResource(Capability capability) {
		CapabilityResource resource = new CapabilityResource();
		resource.setId(capability.getID());
		resource.setName(capability.getName());
		resource.setNotes(capability.getNotes());
		resource.setDescription(capability.getDescription());
		return resource;
	}

}
