package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */

@WebSafeModel
public class APIKeyListModel
{
	private List<APIKeyModel> apiKeyModels;

	public APIKeyListModel(List<APIKeyModel> keyModels)
	{
		apiKeyModels = keyModels;
	}

	public List<APIKeyModel> getKeys()
	{
		return apiKeyModels;
	}
}
