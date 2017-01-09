package com.wahwahnetworks.platform.models.web;

/**
 * Created by Justin on 2/6/2015.
 */
public class ProductVersionInternalWebModel extends ProductVersionWebModel
{
	private String gitBranchName;
	private String lastCommitId;
	private Boolean isSyncedToBranch;

	public String getGitBranchName()
	{
		return gitBranchName;
	}

	public void setGitBranchName(String gitBranchName)
	{
		this.gitBranchName = gitBranchName;
	}

	public String getLastCommitId()
	{
		return lastCommitId;
	}

	public void setLastCommitId(String lastCommitId)
	{
		this.lastCommitId = lastCommitId;
	}

	public Boolean getIsSyncedToBranch()
	{
		return isSyncedToBranch;
	}

	public void setIsSyncedToBranch(Boolean isSyncedToBranch)
	{
		this.isSyncedToBranch = isSyncedToBranch;
	}
}
