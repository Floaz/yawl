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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.OrgGroup;
import org.yawlfoundation.yawl.resourcing.resource.Position;

import javax.annotation.Resource;


/**
 * The controller for managing positions.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class PositionsWriteController {

	/**
	 ** Services ************************************************************
	 **/

	@Resource
    private ResourceManager resourceManager;
	
	@Resource
    private AuthorizationChecker authorizationChecker;


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
					method=RequestMethod.POST)
	public PositionResource addNewPosition(@RequestBody NewPositionRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Position)) {
            throw new ConflictException("Positions are not editable!");
        }
       
        if(getOrgDataSet().isKnownPositionName(resource.getName())) {
        	throw new AlreadyExistsException("Position with this name already exists!");
        }
        
        Position position = new Position() ;
        position.setLabel(resource.getName());
        position.setDescription(resource.getDescription());
        position.setNotes(resource.getNotes());
        
        if(resource.getBelongsToOrgGroup() != null && !resource.getBelongsToOrgGroup().isEmpty()) {
	        OrgGroup orgGroup = getOrgDataSet().getOrgGroup(resource.getBelongsToOrgGroup());
	        if(orgGroup == null) {
				throw new ConflictException("Position with the id that is given with belongsToOrgGroup does not exist!");
			}
	        position.setOrgGroup(orgGroup);
	        position.set_orgGroupID(orgGroup.getID());
        }

        if(resource.getReportsTo() != null && !resource.getReportsTo().isEmpty()) {
            Position reportsToPosition = getOrgDataSet().getPosition(resource.getReportsTo());
            if(reportsToPosition == null) {
                throw new ConflictException("Position with the id that is given with reportsTo does not exist!");
            }
            position.setReportsTo(reportsToPosition);
            position.set_reportsToID(reportsToPosition.getID());
        }

        getOrgDataSet().addPosition(position);

        return PositionResourceTransformer.transformToResource(position);
	}


	@RequestMapping(value="/position/{id}",
			        method=RequestMethod.PUT)
	public void editPosition(@PathVariable("id") String id,
                         @RequestBody EditPositionRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Position)) {
            throw new ConflictException("Positions are not editable!");
        }

        Position position = getOrgDataSet().getPosition(id);

		if(position == null) {
			throw new NotFoundException("No such positions with this id!");
		}

        if(resource.getName() != null && !resource.getName().equals(position.getName())) {
            if(getOrgDataSet().isKnownPositionName(resource.getName())) {
                throw new AlreadyExistsException("Position with this name already exists!");
            }
            position.setLabel(resource.getName());
        }

        if(resource.getDescription() != null) {
            position.setDescription(resource.getDescription());
        }

        if(resource.getNotes() != null) {
            position.setNotes(resource.getNotes());
        }

        if(resource.getBelongsToOrgGroup() != null) {
		    if(resource.getBelongsToOrgGroup().isEmpty()) {
                position.setOrgGroup((OrgGroup) null);
                position.set_orgGroupID(null);
            } else {
                OrgGroup orgGroup = getOrgDataSet().getOrgGroup(resource.getBelongsToOrgGroup());
                if (orgGroup == null) {
                    throw new ConflictException("Position with the id that is given with belongsToOrgGroup does not exist!");
                }
                position.setOrgGroup(orgGroup);
                position.set_orgGroupID(orgGroup.getID());
            }
        }

        if(resource.getReportsTo() != null) {
		    if(resource.getReportsTo().isEmpty()) {
                position.setReportsTo((Position) null);
                position.set_reportsToID(null);
            } else {
                Position reportsToPosition = getOrgDataSet().getPosition(resource.getReportsTo());
                if (reportsToPosition == null) {
                    throw new ConflictException("Position with the id that is given with reportsTo does not exist!");
                }
                position.setReportsTo(reportsToPosition);
                position.set_reportsToID(reportsToPosition.getID());
            }
        }

        getOrgDataSet().updatePosition(position);
	}


    @RequestMapping(value="/position/{id}",
                    method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Position)) {
            throw new ConflictException("Positions are not editable!");
        }

        boolean result = getOrgDataSet().removePosition(id);

        if(!result) {
            throw new NotFoundException("No such positions with this id!");
        }
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


    public class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }


    public class AlreadyExistsException extends ConflictException {
        public AlreadyExistsException(String message) {
            super(message);
        }
    }


    @ExceptionHandler(value = { ConflictException.class })
    @ResponseStatus (HttpStatus.CONFLICT)
    protected ErrorDetail handleConflictException(ConflictException ex) {
        return new ErrorDetail(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

}
