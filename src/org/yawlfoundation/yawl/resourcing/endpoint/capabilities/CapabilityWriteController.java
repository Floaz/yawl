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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Capability;

import javax.annotation.Resource;


/**
 * The controller for managing capabilities.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class CapabilityWriteController {

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

	@RequestMapping(value={"/capability", "/capabilities"},
					method=RequestMethod.POST)
	public CapabilityResource addNewCapability(@RequestBody NewCapabilityRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Capability)) {
            throw new ConflictException("Capabilities are not editable!");
        }
       
        if(getOrgDataSet().isKnownCapabilityName(resource.getName())) {
        	throw new AlreadyExistsException("Capability with this name already exists!");
        }
        
        Capability capability = new Capability() ;
        capability.setLabel(resource.getName());
        capability.setDescription(resource.getDescription());
        capability.setNotes(resource.getNotes());

        getOrgDataSet().addCapability(capability);

        return CapabilityResourceTransformer.transformToResource(capability);
	}


	@RequestMapping(value="/capability/{id}",
			        method=RequestMethod.PUT)
	public void editCapability(@PathVariable("id") String id,
                         @RequestBody EditCapabilityRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Capability)) {
            throw new ConflictException("Capabilities are not editable!");
        }

        Capability capability = getOrgDataSet().getCapability(id);

		if(capability == null) {
			throw new NotFoundException("No such capability with this id!");
		}

        if(resource.getName() != null && !resource.getName().equals(capability.getName())) {
            if(getOrgDataSet().isKnownCapabilityName(resource.getName())) {
                throw new AlreadyExistsException("Capability with this name already exists!");
            }
            capability.setLabel(resource.getName());
        }

        if(resource.getDescription() != null) {
            capability.setDescription(resource.getDescription());
        }

        if(resource.getNotes() != null) {
            capability.setNotes(resource.getNotes());
        }

        getOrgDataSet().updateCapability(capability);
	}


    @RequestMapping(value="/capability/{id}",
                    method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.Capability)) {
            throw new ConflictException("Capabilities are not editable!");
        }

        boolean result = getOrgDataSet().removeCapability(id);

        if(!result) {
            throw new NotFoundException("No such capability with this id!");
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
