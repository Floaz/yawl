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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * A data transfer object that represents a list of capability names and is used
 * for passing across various HTTP interfaces.
 *
 * @author Philipp Thomas
 * @date 10/11/2016
 */
@XmlRootElement(name = "capabilities")
@XmlAccessorType(XmlAccessType.FIELD)
public class CapabilityNamesListResource implements Serializable {

	@XmlElement(name = "capability")
	private List<String> capabilities;


	public CapabilityNamesListResource() {
		capabilities = new LinkedList<String>();
	}


	public CapabilityNamesListResource(List<String> capabilities) {
		this.capabilities = capabilities;
	}


	public List<String> getCapabilities() {
		return capabilities;
	}


	public void setCapabilities(List<String> capabilities) {
		this.capabilities = capabilities;
	}


	public boolean add(String e) {
		return capabilities.add(e);
	}


	public boolean addAll(Collection<? extends String> c) {
		return capabilities.addAll(c);
	}

}
