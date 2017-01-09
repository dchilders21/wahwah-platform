package com.wahwahnetworks.platform.models.facebook;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by Justin on 6/10/2014.
 */

@WebSafeModel
public class FacebookGraphModel<T>
{
	private List<T> data;
	private FacebookGraphPagingModel paging;

	public List<T> getData()
	{
		return data;
	}

	public void setData(List<T> data)
	{
		this.data = data;
	}

	public FacebookGraphPagingModel getPaging()
	{
		return paging;
	}

	public void setPaging(FacebookGraphPagingModel paging)
	{
		this.paging = paging;
	}
}
