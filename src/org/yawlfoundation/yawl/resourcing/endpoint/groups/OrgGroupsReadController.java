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
package org.yawlfoundation.yawl.resourcing.endpoint.groups;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.OrgGroup;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading organizational groups.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class OrgGroupsReadController {

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

	@RequestMapping(value={"/group", "/groups"},
					method=RequestMethod.GET)
	public OrgGroupsListResource getAllOrgGroups() {
		authorizationChecker.checkForRole("ADMIN");

		Set<OrgGroup> orgGroups = getOrgDataSet().getOrgGroups();
		List<OrgGroupResource> orgGroupResources = OrgGroupResourceTransformer.transformToResources(orgGroups);
		return new OrgGroupsListResource(orgGroupResources);
	}


    @RequestMapping(value={"/groups/identifiers"},
					method=RequestMethod.GET)
    public OrgGroupIdentifiersListResource getAllOrgGroupIds() {
		authorizationChecker.checkForRole("ADMIN");

        Set<OrgGroup> orgGroups = getOrgDataSet().getOrgGroups();
        List<String> orgGroupIds = OrgGroupResourceTransformer.transformToIds(orgGroups);
        return new OrgGroupIdentifiersListResource(orgGroupIds);
    }


    @RequestMapping(value={"/groups/names"},
            		method=RequestMethod.GET)
    public OrgGroupNamesListResource getAllOrgGroupNames() {
		authorizationChecker.checkForRole("ADMIN");

        Set<OrgGroup> orgGroups = getOrgDataSet().getOrgGroups();
        List<String> orgGroupIds = OrgGroupResourceTransformer.transformToNames(orgGroups);
        return new OrgGroupNamesListResource(orgGroupIds);
    }


	@RequestMapping(value="/group/{id}",
					method=RequestMethod.GET)
	public OrgGroupResource getOrgGroupById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		OrgGroup orgGroup = getOrgDataSet().getOrgGroup(id);

		if(orgGroup == null) {
			throw new NotFoundException("No such org groups with this id!");
		}

		return OrgGroupResourceTransformer.transformToResource(orgGroup);
	}


	@RequestMapping(value="/group/by-name/{name}",
			method=RequestMethod.GET)
	public OrgGroupResource getOrgGroupByName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

		OrgGroup orgGroup = getOrgDataSet().getOrgGroupByLabel(name);

		if(orgGroup == null) {
			throw new NotFoundException("No such org groups with this label!");
		}

		return OrgGroupResourceTransformer.transformToResource(orgGroup);
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
