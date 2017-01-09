package com.wahwahnetworks.platform.models;

/**
 * Created by Justin on 6/1/2014.
 */
public class ToolbarUploadInfoModel
{
	private String gitBranchName;
	private String destinationFolder;
	private String commitId;
	private String lastCommitId;
	private String stompConnectionId;

	public String getGitBranchName()
	{
		return gitBranchName;
	}

	public void setGitBranchName(String gitBranchName)
	{
		this.gitBranchName = gitBranchName;
	}

	public String getDestinationFolder()
	{
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder)
	{
		this.destinationFolder = destinationFolder;
	}

	public String getCommitId()
	{
		return commitId;
	}

	public void setCommitId(String commitId)
	{
		this.commitId = commitId;
	}

	public String getStompConnectionId()
	{
		return stompConnectionId;
	}

	public void setStompConnectionId(String stompConnectionId)
	{
		this.stompConnectionId = stompConnectionId;
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
