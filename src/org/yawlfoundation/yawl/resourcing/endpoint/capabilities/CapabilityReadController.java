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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Capability;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading capabilities.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class CapabilityReadController {

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

	@RequestMapping(value={"/capability", "/capabilities"},
					method=RequestMethod.GET)
	public CapabilityListResource getAllCapabilities() {
		authorizationChecker.checkForRole("ADMIN");

		Set<Capability> capabilities = getOrgDataSet().getCapabilities();
		List<CapabilityResource> capabilityResources = CapabilityResourceTransformer.transformToResources(capabilities);
		return new CapabilityListResource(capabilityResources);
	}


    @RequestMapping(value={"/capabilities/identifiers"},
					method=RequestMethod.GET)
    public CapabilityIdentifiersListResource getAllCapabilityIds() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Capability> capabilities = getOrgDataSet().getCapabilities();
        List<String> capabilityIds = CapabilityResourceTransformer.transformToIds(capabilities);
        return new CapabilityIdentifiersListResource(capabilityIds);
    }


    @RequestMapping(value={"/capabilities/names"},
            		method=RequestMethod.GET)
    public CapabilityNamesListResource getAllCapabilityNames() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Capability> capabilities = getOrgDataSet().getCapabilities();
        List<String> capabilityIds = CapabilityResourceTransformer.transformToNames(capabilities);
        return new CapabilityNamesListResource(capabilityIds);
    }


	@RequestMapping(value="/capability/{id}",
					method=RequestMethod.GET)
	public CapabilityResource getCapabilityById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Capability capability = getOrgDataSet().getCapability(id);

		if(capability == null) {
			throw new NotFoundException("No such capability with this id!");
		}

		return CapabilityResourceTransformer.transformToResource(capability);
	}


	@RequestMapping(value="/capability/by-name/{name}",
			method=RequestMethod.GET)
	public CapabilityResource getCapabilityByName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

		Capability capability = getOrgDataSet().getCapabilityByLabel(name);

		if(capability == null) {
			throw new NotFoundException("No such capability with this label!");
		}

		return CapabilityResourceTransformer.transformToResource(capability);
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
