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
package org.yawlfoundation.yawl.resourcing.endpoint.assets;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * A data transfer object that represents a role and is used for passing
 * across various HTTP interfaces.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
@XmlRootElement(name = "assetcategory")
public class AssetCategoryResource implements Serializable {

    private String  id;
    private String  name;
    private String  description;
    private String  notes;
    private List<AssetSubCategoryResource>  subCategories = new LinkedList<AssetSubCategoryResource>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public List<AssetSubCategoryResource> getSubCategories() {
        return subCategories;
    }


    public void setSubCategories(List<AssetSubCategoryResource> subCategories) {
        this.subCategories = subCategories;
    }

}
