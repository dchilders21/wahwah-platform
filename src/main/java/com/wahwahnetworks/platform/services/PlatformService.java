package com.wahwahnetworks.platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by jhaygood on 2/23/15.
 */

@Service
public class PlatformService
{

	private String version;
	private String commitId;

	@Autowired
	public PlatformService(ApplicationContext applicationContext) throws IOException
	{
		Resource platformPropertiesResource = applicationContext.getResource("classpath:wahwahplatform.properties");

		Properties properties = new Properties();
		properties.load(platformPropertiesResource.getInputStream());

		version = properties.getProperty("version");
		commitId = properties.getProperty("git_commit_id");
	}

	public String getPlatformVersion()
	{
		return version;
	}

	public String getCommitId()
	{
		return commitId;
	}
}
