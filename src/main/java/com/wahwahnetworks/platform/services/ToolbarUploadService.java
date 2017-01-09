package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.ProductVersion;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.entities.enums.UploadStatus;
import com.wahwahnetworks.platform.models.ToolbarUploadInfoModel;
import com.wahwahnetworks.platform.models.UploadMessageStatus;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.git.GitFileListModel;
import com.wahwahnetworks.platform.models.web.GitBranchListModel;
import com.wahwahnetworks.platform.models.web.ToolbarUploadCredentialTestStatusModel;
import com.wahwahnetworks.platform.models.web.ToolbarUploadGitFileListModel;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Justin on 5/18/2014.
 */

@Service
public class ToolbarUploadService
{

	private static final Logger logger = Logger.getLogger(ToolbarUploadService.class);

	@Autowired
	private GitService gitService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private FileMinificationService fileMinificationService;

	@Autowired
	private ProductVersionService productVersionService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private FilePublisherService filePublisherService;

	@Autowired
	private FileWildcardService fileWildcardService;

	@Transactional
	public GitBranchListModel listGitBranches() throws Exception
	{

		Iterable<ProductVersion> productVersions = productVersionService.getAllNonObsoleteVersions();
		Set<String> branchSet = new HashSet<>();

		for (ProductVersion productVersion : productVersions)
		{
			branchSet.add(productVersion.getGitBranchName());
		}

		GitBranchListModel gitBranchListModel = new GitBranchListModel();
		gitBranchListModel.setGitBranches(branchSet);

		return gitBranchListModel;

	}

	@Transactional
	public ToolbarUploadGitFileListModel listNewFilesInGitBranch(String gitBranch, String currentCommitId) throws Exception
	{
		ProductVersion productVersion = productVersionService.findByGitBranchName(gitBranch);
		return listNewFilesInGitBranch(gitBranch, currentCommitId, productVersion.getLastCommitId());
	}


	@Transactional
	public ToolbarUploadGitFileListModel listNewFilesInGitBranch(String gitBranch, String currentCommitId, String previousCommitId) throws Exception
	{
		GitFileListModel fileListModel = gitService.listNewFilesInBranch(gitBranch, currentCommitId, previousCommitId);

		return convertFileListHelper(fileListModel, previousCommitId);
	}


	@Transactional
	public ToolbarUploadGitFileListModel listAllFilesInGitBranch(String gitBranch, String commitId) throws Exception
	{
		GitFileListModel fileListModel = gitService.listAllFilesInBranch(gitBranch, commitId);

		return convertFileListHelper(fileListModel, null);
	}

	private ToolbarUploadGitFileListModel convertFileListHelper(GitFileListModel fileListModel, String previousCommitId) throws Exception
	{
		Set<String> newFiles = new TreeSet<>();

		for (String file : fileListModel.getFiles())
		{

			boolean shouldSkipFile = fileWildcardService.doesFileNameMatchList(file, "skip_upload_list");

			if (!shouldSkipFile)
			{
				newFiles.add(file);
			}
		}

		ToolbarUploadGitFileListModel toolbarUploadGitFileListModel = new ToolbarUploadGitFileListModel();
		toolbarUploadGitFileListModel.setFiles(newFiles);
		toolbarUploadGitFileListModel.setCommitId(fileListModel.getCommitId());
		if (previousCommitId != null)
			toolbarUploadGitFileListModel.setLastCommitId(previousCommitId);

		return toolbarUploadGitFileListModel;
	}

	@Transactional
	@Async
	public void uploadFiles(UserModel userModel, ToolbarUploadInfoModel fileInfoModel) throws Exception
	{

		FTPClient ftpClient = null;
		FTPClient originalFtpClient = null;

		try
		{

			ftpClient = filePublisherService.getEdgeCastFTPClient();
			originalFtpClient = filePublisherService.getEdgeCastFTPClient();

			ProductVersion productVersion = productVersionService.findByGitBranchName(fileInfoModel.getGitBranchName());

			ToolbarUploadGitFileListModel fileListModel = listNewFilesInGitBranch(fileInfoModel.getGitBranchName(), fileInfoModel.getCommitId(), fileInfoModel.getLastCommitId());

			if (fileInfoModel.getDestinationFolder().equals("prod"))
			{
				productVersionService.setLastCommit(fileInfoModel.getGitBranchName(), fileInfoModel.getCommitId(), Instant.now());
			}

			String toolbarVersion = String.format("Published Toolbar Version: %s on Branch: %s With Commit: %s", productVersion.getVersionName(), productVersion.getGitBranchName(), fileInfoModel.getCommitId());
			auditService.addAuditEntry(userModel, AuditActionTypeEnum.TOOLBAR_PUBLISH, toolbarVersion, "");

			String basePath = "product/_release/" + fileInfoModel.getDestinationFolder() + "/" + productVersion.getVersionName() + "/";
			String originalBasePath = basePath + "_original/"; // For unaltered Sources

			for (String file : fileListModel.getFiles())
			{
				byte[] fileContents = gitService.getFileContents(fileInfoModel.getGitBranchName(), file, fileInfoModel.getCommitId());

				String path = basePath + file;
				String originalPath = originalBasePath + file;

				SimpMessagingTemplate brokerMessagingTemplate = applicationContext.getBean(SimpMessagingTemplate.class);

				UploadMessageStatus messageStatus;

				messageStatus = new UploadMessageStatus(file, UploadStatus.UPLOAD);
				brokerMessagingTemplate.convertAndSend("/topic/" + fileInfoModel.getStompConnectionId(), messageStatus);

				byte[] minifiedFileContents = fileMinificationService.compressFile(file, fileContents);
				filePublisherService.uploadFileToEdgeCast(ftpClient, path, minifiedFileContents);
				filePublisherService.uploadFileToEdgeCast(originalFtpClient, originalPath, fileContents); // For unaltered sources

				messageStatus = new UploadMessageStatus(file, UploadStatus.DONE);
				brokerMessagingTemplate.convertAndSend("/topic/" + fileInfoModel.getStompConnectionId(), messageStatus);
			}

			String purgePath = basePath + "*";

			filePublisherService.purgeFileOnEdgeCast(userModel, purgePath);

		}
		catch (Exception ex)
		{
			logger.error("Error while uploading files", ex);
		}
		finally
		{
			if (ftpClient != null)
			{
				ftpClient.disconnect();
			}

			if (originalFtpClient != null)
			{
				originalFtpClient.disconnect();
			}
		}
	}

	public ToolbarUploadCredentialTestStatusModel testCurrentCredentials() throws Exception
	{

		return testCredentials();
	}

	public ToolbarUploadCredentialTestStatusModel testCredentials() throws Exception
	{

		ToolbarUploadCredentialTestStatusModel statusModel = new ToolbarUploadCredentialTestStatusModel();

		statusModel.setDidGitCredentialsWork(true);
		statusModel.setDidEdgeCastCredentialsWork(true);

		return statusModel;
	}
}
