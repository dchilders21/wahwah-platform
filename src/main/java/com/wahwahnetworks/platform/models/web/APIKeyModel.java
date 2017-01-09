package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */

@WebSafeModel
public class APIKeyModel
{
	private int widgetId;
	private String widgetName;
	private String apiKey;

	public int getWidgetId()
	{
		return widgetId;
	}

	public void setWidgetId(int widgetId)
	{
		this.widgetId = widgetId;
	}

	public String getWidgetName()
	{
		return widgetName;
	}

	public void setWidgetName(String widgetName)
	{
		this.widgetName = widgetName;
	}

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
}
