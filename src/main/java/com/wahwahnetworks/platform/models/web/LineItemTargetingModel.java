package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleDisplayType;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleType;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@WebSafeModel
public class LineItemTargetingModel
{
	@JsonProperty("id")
	Integer id;

	@JsonProperty("name")
	String name;

	@JsonProperty("targetable_type")
	LineItemTargetingRuleType type;

	@JsonProperty("line_item_id")
	Integer lineItemId;

	/* Only one of the following will be set */
	@JsonProperty("network_id")
	private
	Integer networkId;

	@JsonProperty("publisher_id")
	Integer publisherId;

	@JsonProperty("site_id")
	Integer siteId;

	@JsonProperty("product_id")
	Integer productId;

	@JsonProperty("display_type")
	LineItemTargetingRuleDisplayType displayType;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
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

	public LineItemTargetingRuleType getType()
	{
		return type;
	}

	public void setType(LineItemTargetingRuleType type)
	{
		this.type = type;
	}

	public Integer getLineItemId()
	{
		return lineItemId;
	}

	public void setLineItemId(Integer lineItemId)
	{
		this.lineItemId = lineItemId;
	}

	public Integer getPublisherId()
	{
		return publisherId;
	}

	public void setPublisherId(Integer publisherId)
	{
		this.publisherId = publisherId;
	}

	public Integer getSiteId()
	{
		return siteId;
	}

	public void setSiteId(Integer siteId)
	{
		this.siteId = siteId;
	}

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public LineItemTargetingRuleDisplayType getDisplayType()
	{
		return displayType;
	}

	public void setDisplayType(LineItemTargetingRuleDisplayType displayType)
	{
		this.displayType = displayType;
	}

	public Integer getNetworkId() {
		return networkId;
	}

	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
}
