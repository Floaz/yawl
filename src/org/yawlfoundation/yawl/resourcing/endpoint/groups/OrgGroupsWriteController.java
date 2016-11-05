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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.OrgGroup;

import javax.annotation.Resource;


/**
 * The controller for managing organizational groups.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class OrgGroupsWriteController {

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

	@RequestMapping(value={"/group", "/groups"},
					method=RequestMethod.POST)
	public OrgGroupResource addNewOrgGroup(@RequestBody NewOrgGroupRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.OrgGroup)) {
            throw new ConflictException("OrgGroups are not editable!");
        }
       
        if(getOrgDataSet().isKnownOrgGroupName(resource.getName())) {
        	throw new AlreadyExistsException("OrgGroup with this name already exists!");
        }
        
        OrgGroup group = new OrgGroup() ;
        group.setLabel(resource.getName());
        group.setDescription(resource.getDescription());
        group.setNotes(resource.getNotes());

        if(resource.getBelongsTo() != null && !resource.getBelongsTo().isEmpty()) {
            OrgGroup belongsToOrgGroup = getOrgDataSet().getOrgGroup(resource.getBelongsTo());
            if(belongsToOrgGroup == null) {
                throw new ConflictException("OrgGroup with the id that is given with belongsTo does not exist!");
            }
            group.setBelongsTo(belongsToOrgGroup);
            group.set_belongsToID(belongsToOrgGroup.getID());
        }

        getOrgDataSet().addOrgGroup(group);

        return OrgGroupResourceTransformer.transformToResource(group);
	}


	@RequestMapping(value="/group/{id}",
			        method=RequestMethod.PUT)
	public void editOrgGroup(@PathVariable("id") String id,
                         @RequestBody EditOrgGroupRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.OrgGroup)) {
            throw new ConflictException("OrgGroups are not editable!");
        }

        OrgGroup group = getOrgDataSet().getOrgGroup(id);

		if(group == null) {
			throw new NotFoundException("No such groups with this id!");
		}

        if(resource.getName() != null && !resource.getName().equals(group.getName())) {
            if(getOrgDataSet().isKnownOrgGroupName(resource.getName())) {
                throw new AlreadyExistsException("OrgGroup with this name already exists!");
            }
            group.setLabel(resource.getName());
        }

        if(resource.getDescription() != null) {
            group.setDescription(resource.getDescription());
        }

        if(resource.getNotes() != null) {
            group.setNotes(resource.getNotes());
        }

        if(resource.getType() != null) {
            group.setGroupType(resource.getType());
        }

        if(resource.getBelongsTo() != null) {
		    if(resource.getBelongsTo().isEmpty()) {
                group.setBelongsTo((OrgGroup) null);
                group.set_belongsToID(null);
            } else {
                OrgGroup belongsToOrgGroup = getOrgDataSet().getOrgGroup(resource.getBelongsTo());
                if (belongsToOrgGroup == null) {
                    throw new ConflictException("OrgGroup with the id that is given with belongsTo does not exist!");
                }
                group.setBelongsTo(belongsToOrgGroup);
                group.set_belongsToID(belongsToOrgGroup.getID());
            }
        }

        getOrgDataSet().updateOrgGroup(group);
	}


    @RequestMapping(value="/group/{id}",
                    method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.OrgGroup)) {
            throw new ConflictException("OrgGroups are not editable!");
        }

        boolean result = getOrgDataSet().removeOrgGroup(id);

        if(!result) {
            throw new NotFoundException("No such groups with this id!");
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
