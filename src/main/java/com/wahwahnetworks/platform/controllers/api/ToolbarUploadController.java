package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.ToolbarUploadCredentialsModel;
import com.wahwahnetworks.platform.models.ToolbarUploadInfoModel;
import com.wahwahnetworks.platform.models.web.GitBranchListModel;
import com.wahwahnetworks.platform.models.web.ToolbarUploadCredentialTestStatusModel;
import com.wahwahnetworks.platform.models.web.ToolbarUploadGitFileListModel;
import com.wahwahnetworks.platform.models.web.ToolbarUploadStatusModel;
import com.wahwahnetworks.platform.services.ToolbarUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Justin on 5/18/2014.
 */

@RestController
@RequestMapping("/api/1.0/toolbar-upload/")
@Scope("request")
public class ToolbarUploadController extends BaseAPIController
{

	@Autowired
	private ToolbarUploadService toolbarUploadService;

	@Autowired
	private SessionModel sessionModel;

	@RequestMapping(value = "/test-credentials/current", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadCredentialTestStatusModel testCurrentCredentials() throws Exception
	{
		return toolbarUploadService.testCurrentCredentials();
	}

	@RequestMapping(value = "/test-credentials", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadCredentialTestStatusModel testCredentials(@RequestBody ToolbarUploadCredentialsModel credentialsModel) throws Exception
	{
		return toolbarUploadService.testCredentials();
	}

	@RequestMapping(value = "/save-credentials", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadCredentialTestStatusModel saveCredentials(@RequestBody ToolbarUploadCredentialsModel credentialsModel) throws Exception
	{
		ToolbarUploadCredentialTestStatusModel statusModel = new ToolbarUploadCredentialTestStatusModel();
		statusModel.setDidEdgeCastCredentialsWork(true);
		statusModel.setDidGitCredentialsWork(true);
		return statusModel;
	}

	@RequestMapping(value = "/list-git-branches", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public GitBranchListModel listGitBranches() throws Exception
	{
		return toolbarUploadService.listGitBranches();
	}

	@RequestMapping(value = "/list-files/{branchName}", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadGitFileListModel listFiles(@PathVariable("branchName") String gitBranchName) throws Exception
	{
		return toolbarUploadService.listNewFilesInGitBranch(gitBranchName, null);
	}

	@RequestMapping(value = "/list-files-all/{branchName}", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadGitFileListModel listFilesAll(@PathVariable("branchName") String gitBranchName) throws Exception
	{
		return toolbarUploadService.listAllFilesInGitBranch(gitBranchName, null);
	}

	@RequestMapping(value = "/upload-files", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ToolbarUploadStatusModel uploadFiles(@RequestBody ToolbarUploadInfoModel fileInfoModel) throws Exception
	{
		toolbarUploadService.uploadFiles(sessionModel.getUser(), fileInfoModel); // This method will be invoked async
		return new ToolbarUploadStatusModel();
	}
}
