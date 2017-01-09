package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.ProductVersion;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.CreateProductVersionModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.GitBranchListModel;
import com.wahwahnetworks.platform.models.web.ProductVersionListModel;
import com.wahwahnetworks.platform.models.web.ProductVersionWebModel;
import com.wahwahnetworks.platform.services.ProductVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 2/6/2015.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/product-versions/")
public class ProductVersionController extends BaseAPIController
{

	@Autowired
	private ProductVersionService productVersionService;

	@Autowired
	private SessionModel sessionModel;

	@RequestMapping(method = RequestMethod.GET, value = "without-checkout")
	// Prevent errors caused by botched jGit implementation so development can continue
	@HasUserRole(UserRoleType.USER)
	public ProductVersionListModel getReleasedProductVersionsWithoutCheckout() throws Exception
	{

		Iterable<ProductVersion> productVersionList = productVersionService.getCurrentVersions();

		List<ProductVersionWebModel> webModelList = new ArrayList<>();

		for (ProductVersion productVersion : productVersionList)
		{
			productVersion.setLastCommitId(null); // Prevent errors from botched JGit implementation
			ProductVersionWebModel webModel = productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
			webModelList.add(webModel);
		}

		return new ProductVersionListModel(webModelList);
	}

	@RequestMapping(method = RequestMethod.GET)
	@HasUserRole(UserRoleType.USER)
	public ProductVersionListModel getReleasedProductVersions() throws Exception
	{

		Iterable<ProductVersion> productVersionList = productVersionService.getCurrentVersions();

		List<ProductVersionWebModel> webModelList = new ArrayList<>();

		for (ProductVersion productVersion : productVersionList)
		{
			ProductVersionWebModel webModel = productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
			webModelList.add(webModel);
		}

		return new ProductVersionListModel(webModelList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "released-future")
	@HasUserRole(UserRoleType.SUPER_USER)
	public ProductVersionListModel getReleasedAndFutureProductVersions() throws Exception
	{
		Iterable<ProductVersion> productVersionList = productVersionService.getAllNonObsoleteVersions();

		List<ProductVersionWebModel> webModelList = new ArrayList<>();

		for (ProductVersion productVersion : productVersionList)
		{
			ProductVersionWebModel webModel = productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
			webModelList.add(webModel);
		}

		return new ProductVersionListModel(webModelList);
	}

	@RequestMapping(method = RequestMethod.POST)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ProductVersionWebModel createProductVersion(@RequestBody CreateProductVersionModel createProductVersionModel) throws Exception
	{
		ProductVersion productVersion = productVersionService.createVersion(createProductVersionModel);
		return productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
	}

	@RequestMapping(method = RequestMethod.GET, value = "git-branches/unclaimed")
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public GitBranchListModel getUnclaimedGitBranches() throws Exception
	{
		return productVersionService.getUnclaimedGitBranches();
	}

	@RequestMapping(method = RequestMethod.GET, value = "default")
	@HasUserRole(UserRoleType.USER)
	public ProductVersionWebModel getDefaultVersion() throws Exception
	{
		ProductVersion productVersion = productVersionService.getDefaultVersion();
		return productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "default")
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ProductVersionWebModel setDefaultVersion(@RequestBody String branchName) throws Exception
	{
		ProductVersion productVersion = productVersionService.setAsDefault(branchName);
		return productVersionService.getWebModelForEntity(sessionModel.getUser(), productVersion);
	}
}
