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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.datastore.orgdata.ResourceDataSet;
import org.yawlfoundation.yawl.resourcing.endpoint.AuthorizationChecker;
import org.yawlfoundation.yawl.resourcing.exception.NotFoundException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanCategory;
import org.yawlfoundation.yawl.resourcing.resource.nonhuman.NonHumanResource;


/**
 * The controller for reading capabilities.
 *
 * @author Philipp Thomas
 * @date 22/11/2016
 */
@RestController
public class AssetReadController {

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

	@RequestMapping(value={"/asset", "/assets"},
					method=RequestMethod.GET)
	public AssetListResource getAllAssets() {
		authorizationChecker.checkForRole("ADMIN");

		Set<NonHumanResource> assets = getOrgDataSet().getNonHumanResources();
		List<AssetResource> resources = AssetResourceTransformer.transformToResources(assets);
		return new AssetListResource(resources);
	}


	@RequestMapping(value="/asset/{id}",
					method=RequestMethod.GET)
	public AssetResource getAssetById(@PathVariable("id") String id) {
		authorizationChecker.checkForRole("ADMIN");

		NonHumanResource asset = getOrgDataSet().getNonHumanResource(id);

		if(asset == null) {
			throw new NotFoundException("No such asset with this id!");
		}

		return AssetResourceTransformer.transformToResource(asset);
	}

    
	@RequestMapping(value={"/assetcategories", "/assetcategory"},
					method=RequestMethod.GET)
	public AssetCategoryListResource getAllCategories() {
		authorizationChecker.checkForRole("ADMIN");

		Set<NonHumanCategory> categories = getOrgDataSet().getNonHumanCategories();
		List<AssetCategoryResource> resources = AssetCategoryResourceTransformer.transformToResources(categories);
		return new AssetCategoryListResource(resources);
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

}
