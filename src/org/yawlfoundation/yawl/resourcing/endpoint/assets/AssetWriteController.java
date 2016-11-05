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

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.ErrorDetail;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;

import javax.annotation.Resource;
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanCategory;
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanResource;
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanSubCategory;


/**
 * The controller for managing assets.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class AssetWriteController {

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

	@RequestMapping(value={"/asset", "/assets"},
					method=RequestMethod.POST)
	public AssetResource addNewAsset(@RequestBody NewAssetRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanResource)) {
            throw new ConflictException("Assets are not editable!");
        }

        if(getOrgDataSet().isKnownNonHumanResourceName(resource.getName())) {
        	throw new AlreadyExistsException("Asset with this name already exists!");
        }

        NonHumanCategory category = getOrgDataSet().getNonHumanCategory(resource.getCategoryId());

        if(category == null) {
            throw new ConflictException("Category does not exist!");
        }

        Optional<NonHumanSubCategory> subCategory = category.getSubCategories()
                                                    .stream()
                                                    .filter((t) -> resource.getSubCategoryId() != null && t.getID() == resource.getSubCategoryId())
                                                    .findFirst();

        if(!subCategory.isPresent()) {
            throw new ConflictException("Subcategory does not exist!");
        }

        NonHumanResource asset = new NonHumanResource() ;
        asset.setName(resource.getName());
        asset.setDescription(resource.getDescription());
        asset.setNotes(resource.getNotes());
        asset.setCategory(category);
        asset.setSubCategory(subCategory.get().getName());

        getOrgDataSet().addNonHumanResource(asset);

        return AssetResourceTransformer.transformToResource(asset);
	}


	@RequestMapping(value="/asset/{id}",
			        method=RequestMethod.PUT)
	public void editAsset(@PathVariable("id") String id,
                         @RequestBody EditAssetRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanResource)) {
            throw new ConflictException("Assets are not editable!");
        }

        NonHumanResource asset = getOrgDataSet().getNonHumanResource(id);

		if(asset == null) {
			throw new NotFoundException("No such asset with this id!");
		}

        if(resource.getName() != null && !resource.getName().equals(asset.getName())) {
            if(getOrgDataSet().isKnownNonHumanResourceName(resource.getName())) {
                throw new AlreadyExistsException("Asset with this name already exists!");
            }
            asset.setName(resource.getName());
        }

        if(resource.getDescription() != null) {
            asset.setDescription(resource.getDescription());
        }

        if(resource.getNotes() != null) {
            asset.setNotes(resource.getNotes());
        }


        if(resource.getCategoryId() != null) {
            NonHumanCategory category = getOrgDataSet().getNonHumanCategory(resource.getCategoryId());

            if(category == null) {
                throw new ConflictException("Category does not exist!");
            }

            Optional<NonHumanSubCategory> subCategory = category.getSubCategories()
                                                        .stream()
                                                        .filter((t) -> t.getID() == resource.getSubCategoryId())
                                                        .findFirst();

            if(!subCategory.isPresent()) {
                throw new ConflictException("Subcategory does not exist!");
            }

            asset.setCategory(category);
            asset.setSubCategory(subCategory.get().getName());
        }

        getOrgDataSet().updateNonHumanResource(asset);
	}


    @RequestMapping(value="/asset/{id}",
                    method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanResource)) {
            throw new ConflictException("Assets are not editable!");
        }

        boolean result = getOrgDataSet().removeNonHumanResource(id);

        if(!result) {
            throw new NotFoundException("No such asset with this id!");
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
