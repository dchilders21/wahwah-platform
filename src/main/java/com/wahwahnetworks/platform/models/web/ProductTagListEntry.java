package com.wahwahnetworks.platform.models.web;

/**
 * Created by Brian.Bober on 12/17/2015.
 */

public class ProductTagListEntry
{
	private String tag;
	private String fileName;
	private Integer productId;

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}
}