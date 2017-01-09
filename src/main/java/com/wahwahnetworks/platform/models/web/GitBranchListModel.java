package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.Set;

/**
 * Created by Justin on 5/18/2014.
 */

@WebSafeModel
public class GitBranchListModel
{

	private Set<String> gitBranches;

	public Set<String> getGitBranches()
	{
		return gitBranches;
	}

	public void setGitBranches(Set<String> gitBranches)
	{
		this.gitBranches = gitBranches;
	}
}
