package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.ProductVersion;
import com.wahwahnetworks.platform.data.entities.enums.ProductType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.data.repos.ProductVersionRepository;
import com.wahwahnetworks.platform.models.CreateProductVersionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.GitBranchListModel;
import com.wahwahnetworks.platform.models.web.ProductVersionInternalWebModel;
import com.wahwahnetworks.platform.models.web.ProductVersionWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Justin on 2/6/2015.
 */

@Service
public class ProductVersionService
{

	@Autowired
	private ProductVersionRepository productVersionRepository;

	@Autowired
	private GitService gitService;

	@Transactional
	public ProductVersion createVersion(CreateProductVersionModel createProductVersionModel)
	{
		ProductVersion productVersion = new ProductVersion();
		productVersion.setGitBranchName(createProductVersionModel.getBranchName());
		productVersion.setVersionName(createProductVersionModel.getVersionName());
		productVersion.setProductType(ProductType.TOOLBAR);
		productVersion.setIsReleased(false);
		productVersion.setIsObsolete(false);
		productVersion.setIsDefault(false);
		productVersion.setLastPublishTime(null);
		productVersion.setLastCommitId(null);

		productVersionRepository.save(productVersion);

		return productVersion;
	}

	@Transactional
	public ProductVersion setLastCommit(String gitBranchName, String commitId, Instant publishTime)
	{

		ProductVersion productVersion = productVersionRepository.findByGitBranchName(gitBranchName);

		if (productVersion != null)
		{
			productVersion.setIsReleased(true);

			if (commitId != null)
			{
				productVersion.setLastCommitId(commitId);
			}

			productVersion.setLastPublishTime(publishTime);

			productVersionRepository.save(productVersion);
		}

		return productVersion;
	}

	@Transactional
	public ProductVersion findByGitBranchName(String gitBranchName)
	{
		return productVersionRepository.findByGitBranchName(gitBranchName);
	}

	@Transactional
	public ProductVersion markObsolete(String gitBranchName)
	{
		ProductVersion productVersion = productVersionRepository.findByGitBranchName(gitBranchName);
		productVersion.setIsObsolete(true);
		productVersionRepository.save(productVersion);
		return productVersion;
	}

	@Transactional
	public ProductVersion setAsDefault(String gitBranchName)
	{

		ProductVersion defaultForVersion = productVersionRepository.findByIsDefaultTrue();

		if (defaultForVersion != null)
		{
			defaultForVersion.setIsDefault(false);
			productVersionRepository.save(defaultForVersion);
		}

		ProductVersion productVersion = productVersionRepository.findByGitBranchName(gitBranchName);
		productVersion.setIsDefault(true);
		productVersionRepository.save(productVersion);

		return productVersion;
	}

	@Transactional
	public ProductVersion getDefaultVersion()
	{
		return productVersionRepository.findByIsDefaultTrue();
	}

	@Transactional
	public Iterable<ProductVersion> getCurrentVersions()
	{
		return productVersionRepository.findByIsObsoleteFalseAndIsReleasedTrue();
	}

	@Transactional
	public Iterable<ProductVersion> getAllVersions()
	{
		return productVersionRepository.findAll();
	}

	@Transactional
	public Iterable<ProductVersion> getAllNonObsoleteVersions()
	{
		return productVersionRepository.findByIsObsoleteFalse();
	}

	public ProductVersionWebModel getWebModelForEntity(UserModel user, ProductVersion productVersion) throws Exception
	{
		ProductVersionWebModel webModel = new ProductVersionWebModel();

		if (user.hasRole(UserRoleType.TOOLBAR_PUBLISHER))
		{
			ProductVersionInternalWebModel internalWebModel = new ProductVersionInternalWebModel();
			webModel = internalWebModel;
			internalWebModel.setLastCommitId(productVersion.getLastCommitId());
			internalWebModel.setGitBranchName(productVersion.getGitBranchName());

			if (productVersion.getLastCommitId() != null)
			{
				String commitId = gitService.getCommitIdForBranch(productVersion.getGitBranchName());
				internalWebModel.setIsSyncedToBranch(productVersion.getLastCommitId().equals(commitId));
			}
			else
			{
				internalWebModel.setIsSyncedToBranch(false);
			}
		}

		webModel.setId(productVersion.getId());

		if (productVersion.getLastPublishTime() != null)
		{
			OffsetDateTime lastPublishTime = OffsetDateTime.ofInstant(productVersion.getLastPublishTime(), ZoneId.of("UTC"));
			webModel.setLastPublishTime(lastPublishTime);
		}

		webModel.setProductType(productVersion.getProductType().name());
		webModel.setVersionName(productVersion.getVersionName());
		webModel.setIsDefault(productVersion.getIsDefault());

		return webModel;
	}

	public GitBranchListModel getUnclaimedGitBranches() throws Exception
	{
		Iterable<ProductVersion> allProductVersions = productVersionRepository.findAll();
		Set<String> allBranches = gitService.listBranches();

		Set<String> unclaimedBranches = new HashSet<>();

		for (String branchName : allBranches)
		{

			boolean hasProductVersion = false;

			for (ProductVersion productVersion : allProductVersions)
			{
				if (productVersion.getGitBranchName().equals(branchName))
				{
					hasProductVersion = true;
					break;
				}
			}

			if (!hasProductVersion)
			{
				unclaimedBranches.add(branchName);
			}
		}

		GitBranchListModel gitBranchListModel = new GitBranchListModel();
		gitBranchListModel.setGitBranches(unclaimedBranches);
		return gitBranchListModel;
	}
}
