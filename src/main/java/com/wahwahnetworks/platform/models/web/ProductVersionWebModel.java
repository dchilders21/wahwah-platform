package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.time.OffsetDateTime;

/**
 * Created by Justin on 2/6/2015.
 */

@WebSafeModel
public class ProductVersionWebModel
{
	private int id;
	private String versionName;
	private String productType;
	private OffsetDateTime lastPublishTime;
	private Boolean isDefault;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public String getProductType()
	{
		return productType;
	}

	public void setProductType(String productType)
	{
		this.productType = productType;
	}

	public OffsetDateTime getLastPublishTime()
	{
		return lastPublishTime;
	}

	public void setLastPublishTime(OffsetDateTime lastPublishTime)
	{
		this.lastPublishTime = lastPublishTime;
	}

	public Boolean getIsDefault()
	{
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}
}
