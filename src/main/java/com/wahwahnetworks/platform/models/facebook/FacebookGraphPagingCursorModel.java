package com.wahwahnetworks.platform.models.facebook;

/**
 * Created by Justin on 6/10/2014.
 */
public class FacebookGraphPagingCursorModel
{
	private String before;
	private String after;

	public String getBefore()
	{
		return before;
	}

	public void setBefore(String before)
	{
		this.before = before;
	}

	public String getAfter()
	{
		return after;
	}

	public void setAfter(String after)
	{
		this.after = after;
	}
}
