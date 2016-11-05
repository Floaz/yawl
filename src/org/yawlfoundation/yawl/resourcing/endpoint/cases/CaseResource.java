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

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


/**
 * A data transfer object that represents workitems and is used for passing
 * across various HTTP interfaces.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
@XmlRootElement(name = "case")
public class CaseResource implements Serializable {

	private String 			id;
	private Specification 	specification;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}


	public static class Specification {
		private String id;
		private String version;
		private String uri;

		public Specification() {
		}

		public Specification(String id, String version, String uri) {
			this.id = id;
			this.version = version;
			this.uri = uri;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}
	}

}
