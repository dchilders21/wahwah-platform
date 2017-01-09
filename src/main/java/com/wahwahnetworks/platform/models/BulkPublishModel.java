package com.wahwahnetworks.platform.models;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by Brian.Bober on 8/25/2015.
 */
@WebSafeModel
public class BulkPublishModel
{
	private List<Integer> productIds;
	private String stompConnectionId;

	public List<Integer> getProductIds()
	{
		return productIds;
	}

	public void setProductIds(List<Integer> productIds)
	{
		this.productIds = productIds;
	}

	public String getStompConnectionId()
	{
		return stompConnectionId;
	}

	public void setStompConnectionId(String stompConnectionId)
	{
		this.stompConnectionId = stompConnectionId;
	}
}
