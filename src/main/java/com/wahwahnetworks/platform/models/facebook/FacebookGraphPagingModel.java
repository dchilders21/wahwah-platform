package com.wahwahnetworks.platform.models.facebook;

/**
 * Created by Justin on 6/10/2014.
 */
public class FacebookGraphPagingModel
{
	private String previous;
	private String next;
	private FacebookGraphPagingCursorModel cursors;

	public String getPrevious()
	{
		return previous;
	}

	public void setPrevious(String previous)
	{
		this.previous = previous;
	}

	public String getNext()
	{
		return next;
	}

	public void setNext(String next)
	{
		this.next = next;
	}

	public FacebookGraphPagingCursorModel getCursors()
	{
		return cursors;
	}

	public void setCursors(FacebookGraphPagingCursorModel cursors)
	{
		this.cursors = cursors;
	}
}
