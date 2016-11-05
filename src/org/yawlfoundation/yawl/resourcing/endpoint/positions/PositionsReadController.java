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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Position;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading positions.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class PositionsReadController {

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

	@RequestMapping(value={"/position", "/positions"},
					method=RequestMethod.GET)
	public PositionsListResource getAllPositions() {
		authorizationChecker.checkForRole("ADMIN");

		Set<Position> positions = getOrgDataSet().getPositions();
		List<PositionResource> positionResources = PositionResourceTransformer.transformToResources(positions);
		return new PositionsListResource(positionResources);
	}


    @RequestMapping(value={"/positions/identifiers"},
					method=RequestMethod.GET)
    public PositionIdentifiersListResource getAllPositionIds() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Position> positions = getOrgDataSet().getPositions();
        List<String> positionIds = PositionResourceTransformer.transformToIds(positions);
        return new PositionIdentifiersListResource(positionIds);
    }


    @RequestMapping(value={"/positions/names"},
            		method=RequestMethod.GET)
    public PositionNamesListResource getAllPositionNames() {
		authorizationChecker.checkForRole("ADMIN");

        Set<Position> positions = getOrgDataSet().getPositions();
        List<String> positionIds = PositionResourceTransformer.transformToNames(positions);
        return new PositionNamesListResource(positionIds);
    }


	@RequestMapping(value="/position/{id}",
					method=RequestMethod.GET)
	public PositionResource getPositionById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Position position = getOrgDataSet().getPosition(id);

		if(position == null) {
			throw new NotFoundException("No such positions with this id!");
		}

		return PositionResourceTransformer.transformToResource(position);
	}


	@RequestMapping(value="/position/by-name/{name}",
			method=RequestMethod.GET)
	public PositionResource getPositionByName(@PathVariable("name") String name) {
		authorizationChecker.checkForRole("ADMIN");

		Position position = getOrgDataSet().getPositionByLabel(name);

		if(position == null) {
			throw new NotFoundException("No such positions with this label!");
		}

		return PositionResourceTransformer.transformToResource(position);
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
