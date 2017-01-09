package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by Justin on 5/19/2014.
 */

@WebSafeModel
public class ToolbarUploadCredentialTestStatusModel
{

	private boolean didGitCredentialsWork;
	private boolean didEdgeCastCredentialsWork;


	public boolean isDidGitCredentialsWork()
	{
		return didGitCredentialsWork;
	}

	public void setDidGitCredentialsWork(boolean didGitCredentialsWork)
	{
		this.didGitCredentialsWork = didGitCredentialsWork;
	}

	public boolean isDidEdgeCastCredentialsWork()
	{
		return didEdgeCastCredentialsWork;
	}

	public void setDidEdgeCastCredentialsWork(boolean didEdgeCastCredentialsWork)
	{
		this.didEdgeCastCredentialsWork = didEdgeCastCredentialsWork;
	}
}
