package com.wahwahnetworks.platform.models.bitbucket;

import java.util.List;

/**
 * Created by Justin on 5/18/2014.
 */
public class ListModel<T>
{

	private int pagelen;
	private int page;
	private String next;
	private long size;

	private List<T> values;

	public int getPagelen()
	{
		return pagelen;
	}

	private void setPagelen(int value)
	{
		pagelen = value;
	}

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public String getNext()
	{
		return next;
	}

	public void setNext(String next)
	{
		this.next = next;
	}

	public List<T> getValues()
	{
		return values;
	}

	public void setValues(List<T> values)
	{
		this.values = values;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}
}
