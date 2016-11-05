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
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanSubCategory;


/**
 * The controller for managing assets.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class AssetCategoryWriteController {

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

	@RequestMapping(value={"/assetcategory", "/assetcategories"},
					method=RequestMethod.POST)
	public AssetCategoryResource addNewAssetCategory(@RequestBody NewAssetCategoryRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanCategory)) {
            throw new ConflictException("Asset category are not editable!");
        }

        if(getOrgDataSet().isKnownNonHumanCategoryName(resource.getName())) {
        	throw new AlreadyExistsException("Asset category with this name already exists!");
        }

        NonHumanCategory category = new NonHumanCategory() ;
        category.setName(resource.getName());
        category.setDescription(resource.getDescription());
        category.setNotes(resource.getNotes());
        category.addSubCategory("None");

        getOrgDataSet().addNonHumanCategory(category);

        return AssetCategoryResourceTransformer.transformToResource(category);
	}


	@RequestMapping(value="/assetcategory/{id}",
			        method=RequestMethod.PUT)
	public void editAssetCategory(@PathVariable("id") String id,
                                  @RequestBody EditAssetCategoryRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanCategory)) {
            throw new ConflictException("Asset categories are not editable!");
        }

        NonHumanCategory category = getOrgDataSet().getNonHumanCategory(id);

		if(category == null) {
			throw new NotFoundException("No such asset category with this id!");
		}

        if(resource.getName() != null && !resource.getName().equals(category.getName())) {
            if(getOrgDataSet().isKnownNonHumanCategoryName(resource.getName())) {
                throw new AlreadyExistsException("Asset category with this name already exists!");
            }
            category.setName(resource.getName());
        }

        if(resource.getDescription() != null) {
            category.setDescription(resource.getDescription());
        }

        if(resource.getNotes() != null) {
            category.setNotes(resource.getNotes());
        }

        getOrgDataSet().updateNonHumanCategory(category);
	}


    @RequestMapping(value="/assetcategory/{id}",
                    method=RequestMethod.DELETE)
    public void deleteAssetCategory(@PathVariable("id") String id) {
        authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanCategory)) {
            throw new ConflictException("Asset categories are not editable!");
        }

        boolean result = getOrgDataSet().removeNonHumanCategory(id);

        if(!result) {
            throw new NotFoundException("No such asset category with this id!");
        }
    }


	@RequestMapping(value={"/assetcategory/{id}/subcategory"},
					method=RequestMethod.POST)
	public void addNewAssetSubCategory(@PathVariable("id") String id,
                                       @RequestBody NewAssetSubCategoryRequest resource) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanCategory)) {
            throw new ConflictException("Asset categories are not editable!");
        }

        NonHumanCategory category = getOrgDataSet().getNonHumanCategory(id);

		if(category == null) {
			throw new NotFoundException("No such asset category with this id!");
		}

        Optional<NonHumanSubCategory> subCategory = category.getSubCategories()
                                                    .stream()
                                                    .filter((t) -> t.getName().equals(resource.getName()))
                                                    .findFirst();

        if(subCategory.isPresent()) {
            throw new ConflictException("Subcategory with this name already exists!");
        }

        category.addSubCategory(resource.getName());

        getOrgDataSet().updateNonHumanCategory(category);
	}


	@RequestMapping(value={"/assetcategory/{id}/subcategory/{subCatId}"},
					method=RequestMethod.DELETE)
	public void deleteAssetSubCategory(@PathVariable("id") String id,
                                       @PathVariable("subCatId") Long subCatId) {
		authorizationChecker.checkForRole("ADMIN");

        if(!resourceManager.getOrgDataSet().isDataEditable(ResourceDataSet.ResUnit.NonHumanCategory)) {
            throw new ConflictException("Asset categories are not editable!");
        }

        NonHumanCategory category = getOrgDataSet().getNonHumanCategory(id);

		if(category == null) {
			throw new NotFoundException("No such asset category with this id!");
		}

        Optional<NonHumanSubCategory> subCategory = category.getSubCategories()
                                                    .stream()
                                                    .filter((t) -> t.getID() == subCatId)
                                                    .findFirst();

        if(!subCategory.isPresent()) {
            throw new ConflictException("Subcategory with this id does not exist!");
        }

        category.removeSubCategory(subCategory.get().getName());

        getOrgDataSet().updateNonHumanCategory(category);
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
