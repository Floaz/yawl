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
package org.yawlfoundation.yawl.resourcing.endpoint.cases;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.util.XNode;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * The controller for reading cases.
 *
 * @author Philipp Thomas
 * @date 16/12/2016
 */
@RestController
public class CaseReadController {

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

	@RequestMapping(value={"/case", "/cases"},
					method=RequestMethod.GET)
	public CaseListResource getAllCases() {
		authorizationChecker.checkForRole("ADMIN");
		return new CaseListResource(getRunningCases());
	}

	@RequestMapping(value="/case/{id}",
					method=RequestMethod.GET)
	public CaseResource getCaseById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		List<CaseResource> caseList = getRunningCases();

		for(CaseResource currentCase : caseList) {
		    if(currentCase.getId().equals(id)) {
                return currentCase;
            }
        }

        throw new NotFoundException("Case with this id does not exist!");
	}


    @RequestMapping(value="/specification/{id}/{version}/cases",
                    method=RequestMethod.GET)
    public List<CaseResource> getRunningCasesBySpecification(@PathVariable("id") String id,
                                                                     @PathVariable("version") String version) {
        authorizationChecker.checkForRole("ADMIN");

        YSpecificationID specId = getSpecificationId(id, version);

        if(specId == null) {
            throw new NotFoundException("Specification with this id does not exist!");
        }

        List<CaseResource> caseList = new LinkedList<CaseResource>();

        for(CaseResource currentCase : getRunningCases()) {
            if(currentCase.getSpecification().getId().equals(specId.getIdentifier())
                    && currentCase.getSpecification().getVersion().equals(specId.getVersionAsString())) {
                caseList.add(currentCase);
            }
        }

        return caseList;
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


    private List<CaseResource> getRunningCases() {
        XNode node = resourceManager.getClients().getAllRunningCases();
        List<CaseResource> caseList = new ArrayList<CaseResource>();
        if(node != null) {
            for(XNode specNode : node.getChildren()) {
                for(XNode caseID : specNode.getChildren()) {
                    CaseResource resource = new CaseResource();
                    resource.setId(caseID.getText());
                    resource.setSpecification(new CaseResource.Specification(specNode.getAttributeValue("identifier"),
                            specNode.getAttributeValue("version"),
                            specNode.getAttributeValue("uri")));
                    caseList.add(resource);
                }
            }
        }
        return caseList;
    }


}
