package com.wahwahnetworks.platform.models.facebook;

/**
 * Created by Justin on 6/10/2014.
 */
public class FacebookInsightsItemValueModel
{
	private Object value;
	private String end_time;

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public String getEnd_time()
	{
		return end_time;
	}

	public void setEnd_time(String endTime)
	{
		this.end_time = endTime;
	}
}
