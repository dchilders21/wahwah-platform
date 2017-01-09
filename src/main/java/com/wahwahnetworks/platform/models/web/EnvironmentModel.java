package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.Environment;

/**
 * Created by Brian.Bober on 8/25/2015.
 */

@WebSafeModel
public class EnvironmentModel
{
	private Environment environmentId;
	private String environmentAlias;

	public Environment getEnvironmentId()
	{
		return environmentId;
	}

	public void setEnvironment(Environment environmentId)
	{
		this.environmentId = environmentId;

		if (environmentId.equals(Environment.PRODUCTION))
			this.environmentAlias = "prod";
		else if (environmentId.equals(Environment.STAGING))
			this.environmentAlias = "qa";
		else
			this.environmentAlias = "dev";
	}

	public String getEnvironmentAlias()
	{
		return environmentAlias;
	}

}
