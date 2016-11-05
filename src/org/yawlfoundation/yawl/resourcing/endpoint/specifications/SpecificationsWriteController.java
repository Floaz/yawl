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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.logging.YLogDataItem;
import org.yawlfoundation.yawl.logging.YLogDataItemList;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.BadRequestException;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;
import org.yawlfoundation.yawl.schema.YSchemaVersion;
import org.yawlfoundation.yawl.util.JDOMUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Set;


/**
 * The controller for changing specifications.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class SpecificationsWriteController {

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
                    method=RequestMethod.POST)
    public void addSpecification(@RequestBody String specificationContent) {
        authorizationChecker.checkForRole("ADMIN");

        int BOF = specificationContent.indexOf("<?xml");
        int EOF = specificationContent.indexOf("</specificationSet>");
        if(BOF == -1 || EOF == -1) {
            throw new BadRequestException("No valid specification file!");
        }

        specificationContent = specificationContent.substring(BOF, EOF + 19) ;

        checkSpecification(specificationContent);

        String result = resourceManager.getClients().uploadSpecification(specificationContent, "");
        if(!resourceManager.successful(result)) {
            processErrorMsg(result);
        }
    }


    @RequestMapping(value="/specification/{id}/{version}/start",
            method=RequestMethod.POST)
    public void startCase(@PathVariable("id") String id, @PathVariable("version") String version) {
        authorizationChecker.checkForRole("ADMIN");

        YSpecificationID specId = getSpecificationId(id, version);

        if(specId == null) {
            throw new NotFoundException("No specification with this id and version!" + id + " " + version);
        }

        try {
            YLogDataItemList logData = new YLogDataItemList(
                    new YLogDataItem("launched", "name", "resourceService", "string"));

            String result = resourceManager.getClients().launchCase(specId, null, logData);

            if(!resourceManager.successful(result)) {
                String errorMsg = result.substring("<failure><reason>".length(), result.length()-"</reason></failure>".length());
                throw new RuntimeException(errorMsg);
            }
        }
        catch(IOException e) {
            throw new RuntimeException("Could not unload specification. "+e.getMessage());
        }
    }


	@RequestMapping(value="/specification/{id}/{version}",
					method=RequestMethod.DELETE)
	public void unloadSpecification(@PathVariable("id") String id, @PathVariable("version") String version) {
		authorizationChecker.checkForRole("ADMIN");

		YSpecificationID specId = getSpecificationId(id, version);

		if(specId == null) {
            throw new NotFoundException("No specification with this id and version!" + id + " " + version);
        }

        try {
            String result = resourceManager.unloadSpecification(specId);
            if(!resourceManager.successful(result)) {
                String errorMsg = result.substring("<failure><reason>".length(), result.length()-"</reason></failure>".length());
                throw new RuntimeException(errorMsg);
            }
        }
        catch(IOException e) {
            throw new RuntimeException("Could not unload specification. "+e.getMessage());
        }
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


    private void processErrorMsg(String msg) {
        Element root = JDOMUtil.stringToElement(msg);
        if(root != null) {
            Element reason = root.getChild("reason");
            if (reason != null) {
                Element messages = reason.getChild("verificationMessages");
                if (messages != null) {
                    for (Element e : messages.getChildren()) {
                        if (e.getName().equals("warning")) {
                            throw new BadRequestException(JDOMUtil.elementToStringDump(e));
                        }
                        else if (e.getName().equals("error")) {
                            throw new BadRequestException(JDOMUtil.elementToStringDump(e));
                        }
                    }
                }
            }
        }
    }


    private void checkSpecification(String specxml) {
        if ((specxml == null) || (specxml.length() == 0)) {
            throw new BadRequestException("Invalid specification file: null or empty contents.");
        }

        YSpecificationID specID = getDescriptors(specxml);
        if (specID == null) {
            throw new BadRequestException("Invalid specification: missing identifier or incorrect version.");
        }
        if(!specID.isValid()) {
            throw new BadRequestException("Invalid specification: missing identifier or incorrect version.");
        }

        Set<SpecificationData> loadedSpecs = resourceManager.getLoadedSpecs();

        if(loadedSpecs == null || loadedSpecs.isEmpty()) {
            return;
        }

        for(SpecificationData spec : loadedSpecs) {
            if(spec.getID().equals(specID)) {
                if (specID.getUri().equals(spec.getSpecURI())) {
                    throw new BadRequestException("This specification is already loaded.");
                } else {
                    throw new BadRequestException("A specification with the same id and " +
                                                    "version (but different name) is already loaded.");
                }
            }
            else if(specID.isPreviousVersionOf(spec.getID())) {
                if (specID.getUri().equals(spec.getSpecURI())) {
                    throw new BadRequestException("A later version of this specification is " +
                                                    "already loaded.");
                } else {
                    throw new BadRequestException("A later version of a specification with the " +
                                                    "same id (but different name) is already loaded.");
                }
            }
            else if(specID.getUri().equals(spec.getSpecURI()) &&
                    (!specID.hasMatchingIdentifier(spec.getID()))) {
                throw new BadRequestException("A specification with the same name, but a different " +
                                                "id, is already loaded. Please change the name and try again.");
            }
        }
    }


    private YSpecificationID getDescriptors(String specxml) {
        YSpecificationID descriptors = null;

        Document doc = JDOMUtil.stringToDocument(specxml);
        if (doc != null) {
            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            YSchemaVersion schemaVersion = YSchemaVersion.fromString(
                    root.getAttributeValue("version"));
            Element specification = root.getChild("specification", ns);

            if (specification != null) {
                String uri = specification.getAttributeValue("uri");
                String version = "0.1";
                String uid = null;
                if (! (schemaVersion == null || schemaVersion.isBetaVersion())) {
                    Element metadata = specification.getChild("metaData", ns);
                    version = metadata.getChildText("version", ns);
                    uid = metadata.getChildText("identifier", ns);
                }
                descriptors = new YSpecificationID(uid, version, uri);
            }
            else {
                throw new RuntimeException("Malformed specification: 'specification' node not found.");
            }
        }
        else {
            throw new RuntimeException("Malformed specification: unable to parse.");
        }

        return descriptors;
    }

}
