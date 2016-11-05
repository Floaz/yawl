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
package org.yawlfoundation.yawl.resourcing.endpoint.workitems;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;



/**
 * A data transfer object that represents workitems and is used for passing
 * across various HTTP interfaces.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
@XmlRootElement(name = "workitem")
public class WorkItemResource implements Serializable {

	private String 			id;
	private String 			uniqueId;
	private Specification 	specification;
    private String 			caseId;
    private Task 			task;
    private String			documentation;
	private String 			status;
	private String 			resourceStatus;
	private String 			enablementTime;
	private String 			firingTime;
	private String 			startTime;
	private String 			completionTime ;
	private String 			timerExpiry ;
	private String 			startedBy;
	private String 			completedBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResourceStatus() {
		return resourceStatus;
	}

	public void setResourceStatus(String resourceStatus) {
		this.resourceStatus = resourceStatus;
	}

	public String getEnablementTime() {
		return enablementTime;
	}

	public void setEnablementTime(String enablementTime) {
		this.enablementTime = enablementTime;
	}

	public String getFiringTime() {
		return firingTime;
	}

	public void setFiringTime(String firingTime) {
		this.firingTime = firingTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public String getTimerExpiry() {
		return timerExpiry;
	}

	public void setTimerExpiry(String timerExpiry) {
		this.timerExpiry = timerExpiry;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public String getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
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

	public static class Task {
		private String id;
		private String name;

		public Task() {
		}

		public Task(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
