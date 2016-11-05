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
package org.yawlfoundation.yawl.resourcing.endpoint.specifications;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading specifications.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class SpecificationsReadController {

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

	@RequestMapping(value={"/specification", "/specifications"},
					method=RequestMethod.GET)
	public SpecificationDataListResource getAllSpecifications() {
		authorizationChecker.checkForRole("ADMIN");

		return new SpecificationDataListResource(
		        SpecificationDataResourceTransformer.transformToResources(
		                                                resourceManager.getSpecList()));
	}


	@RequestMapping(value="/specification/{id}",
					method=RequestMethod.GET)
	public SpecificationDataListResource getSpecificationsById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		Set<SpecificationData> specList = resourceManager.getSpecList();
        List<SpecificationDataResource> resources = new LinkedList<SpecificationDataResource>();

		for(SpecificationData spec : specList) {
		    if(spec.getID().getIdentifier().equalsIgnoreCase(id)) {
                resources.add(SpecificationDataResourceTransformer.transformToResource(spec));
            }
        }

		return new SpecificationDataListResource(resources);
	}


    @RequestMapping(value="/specification/{id}/{version}",
            method=RequestMethod.GET)
    public SpecificationDataResource getSpecificationsById(@PathVariable("id") String id,
                                                               @PathVariable("version") String version) {
        authorizationChecker.checkForRole("ADMIN");

        YSpecificationID specId = getSpecificationId(id, version);
        SpecificationData specData = resourceManager.getSpecData(specId);

        if(specData == null) {
            throw new NotFoundException("Specification with this id does not exist!");
        }

        return SpecificationDataResourceTransformer.transformToResource(specData);
    }


    @RequestMapping(value="/specification/{id}/{version}/file",
                    method=RequestMethod.GET)
    public String getSpecificationXmlById(@PathVariable("id") String id,
                                                           @PathVariable("version") String version) {
        authorizationChecker.checkForRole("ADMIN");

        YSpecificationID specId = getSpecificationId(id, version);
        SpecificationData specData = resourceManager.getSpecData(specId);

        if(specData == null) {
            throw new NotFoundException("Specification with this id does not exist!");
        }

        return specData.getAsXML();
    }


    private YSpecificationID getSpecificationId(String id, String version) {
        Set<SpecificationData> specList = resourceManager.getSpecList();

        for(SpecificationData spec : specList) {
            if(spec.getID().getIdentifier().equalsIgnoreCase(id)
                    && spec.getID().getVersionAsString().equalsIgnoreCase(version)) {
                return spec.getID();
            }
        }
        return null;
    }

}
