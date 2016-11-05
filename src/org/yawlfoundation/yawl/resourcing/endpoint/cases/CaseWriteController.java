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
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.eventlog.EventLogger;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.resourcing.resource.Participant;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;

import javax.annotation.Resource;
import java.io.IOException;
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
public class CaseWriteController {

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


	@RequestMapping(value="/case/{id}",
					method=RequestMethod.DELETE)
	public void cancelCase(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		List<CaseResource> caseList = getRunningCases();

		for(CaseResource currentCase : caseList) {
		    if(currentCase.getId().equals(id)) {
		        try {
                    String result = resourceManager.cancelCase(id, null);
                    if(!resourceManager.successful(result)) {
                        String errorMsg = result.substring("<failure><reason>".length(), result.length()-"</reason></failure>".length());
                        throw new RuntimeException(errorMsg);
                    }
                    return;
                }
                catch(IOException e) {
                    throw new RuntimeException("Could not cancel case: "+e.getMessage());
                }
            }
        }

        throw new NotFoundException("Case with this id does not exist!");
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
