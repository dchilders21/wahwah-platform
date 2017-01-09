package com.wahwahnetworks.platform.models.facebook;

import java.util.List;

/**
 * Created by Justin on 6/10/2014.
 */
public class FacebookInsightsItemModel
{
	private String id;
	private String name;
	private String period;
	private List<FacebookInsightsItemValueModel> values;
	private String title;
	private String description;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPeriod()
	{
		return period;
	}

	public void setPeriod(String period)
	{
		this.period = period;
	}

	public List<FacebookInsightsItemValueModel> getValues()
	{
		return values;
	}

	public void setValues(List<FacebookInsightsItemValueModel> values)
	{
		this.values = values;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
