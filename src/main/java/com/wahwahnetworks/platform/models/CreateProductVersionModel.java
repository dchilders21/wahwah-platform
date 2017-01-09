package com.wahwahnetworks.platform.models;

/**
 * Created by jhaygood on 2/8/15.
 */
public class CreateProductVersionModel
{
	private String versionName;
	private String branchName;

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public String getBranchName()
	{
		return branchName;
	}

	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}
}
