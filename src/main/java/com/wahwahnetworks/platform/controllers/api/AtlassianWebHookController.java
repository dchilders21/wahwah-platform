package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.services.GitService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("request")
@RequestMapping("/atlassian") // can't figure out how to make this just /atlassian/...
@RestNoAuthentication
/**
 * Created by Brian.Bober on 12/7/2015.
 */

/*
Description: Just updates Git repo every time there's a check-in
Uses webhooks push from Atlassian Bitbucket
Just be dumb for now, and update everyhting. Maybe in future only check what changed

Put in bitbucket > settings > webhooks:
http://staging.dev.wahwahnetworks.com/atlassian/bitbucket-push
http://app.redpandaplatform.com/atlassian/bitbucket-push
 */
public class AtlassianWebHookController extends BaseAPIController
{

	@Autowired
	GitService gitService;

	private static final Logger logger = Logger.getLogger(AtlassianWebHookController.class);

	@RequestMapping(value = "/bitbucket-push", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void pushBitBucket() throws Exception
	{
		logger.info("An Atlassian Bitbucket push was received. Pulling Git changes");

		gitService.updateGitRepoAsync();
	}
}
