package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.Set;

/**
 * Created by Justin on 6/1/2014.
 */

@WebSafeModel
public class ToolbarUploadGitFileListModel
{
	private Set<String> files;
	private String commitId;
	private String lastCommitId;

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

	public String getLastCommitId()
	{
		return lastCommitId;
	}

	public void setLastCommitId(String lastCommitId)
	{
		this.lastCommitId = lastCommitId;
	}
}
