package com.wahwahnetworks.platform.models.git;

import java.util.Set;

/**
 * Created by Justin on 6/1/2014.
 */
public class GitFileListModel
{
	private Set<String> files;
	private String commitId;

	public Set<String> getFiles()
	{
		return files;
	}

	public void setFiles(Set<String> files)
	{
		this.files = files;
	}

	public String getCommitId()
	{
		return commitId;
	}

	public void setCommitId(String commitId)
	{
		this.commitId = commitId;
	}
}
