package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.lib.GitManager;
import com.wahwahnetworks.platform.models.git.GitFileListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Justin on 5/30/2014.
 */

@Service
public class GitService
{

	private static final Object gitLock = new Object();

	@Autowired
	private GitManager gitManager;

	public Set<String> listBranches() throws Exception
	{
		synchronized (gitLock)
		{
			return gitManager.listBranches();
		}
	}

	public GitFileListModel listNewFilesInBranch(String gitBranchName, String currentCommitId, String lastCommitId) throws Exception
	{
		synchronized (gitLock)
		{
			return gitManager.listNewFilesInBranch(gitBranchName, currentCommitId, lastCommitId);
		}
	}

	public GitFileListModel listAllFilesInBranch(String gitBranchName, String commitId) throws Exception
	{
		synchronized (gitLock)
		{
			return gitManager.listAllFilesInBranch(gitBranchName, commitId);
		}
	}

	public byte[] getFileContents(String gitBranchName, String fileName, String commitId) throws Exception
	{
		synchronized (gitLock)
		{
			return gitManager.getFileContents(gitBranchName, fileName, commitId);
		}
	}

	public String getCommitIdForBranch(String gitBranchName) throws Exception
	{
		synchronized (gitLock)
		{
			return gitManager.getCommitIdForBranch(gitBranchName);
		}
	}

	@Scheduled(fixedDelay = 3000000)
	public void updateGitRepo() throws Exception
	{
		synchronized (gitLock)
		{
			gitManager.update();
		}
	}

	@Async
	public void updateGitRepoAsync() throws Exception
	{
		updateGitRepo();
	}

}
