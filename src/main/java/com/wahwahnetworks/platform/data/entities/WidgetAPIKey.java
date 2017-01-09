package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */

@Entity
@Table(name = "api_keys")
public class WidgetAPIKey
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "widget_id")
	private int widgetId;

	@Column(name = "widget_name")
	private String widgetName;

	@Column(name = "api_key")
	private String apiKey;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

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
