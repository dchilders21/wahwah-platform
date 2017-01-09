package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
		@JsonSubTypes.Type(value = PublisherInternalWebModel.class, name = "InternalAccount")
)
public class PublisherWebModel extends AccountWebModel
{

	// Only one or other will be set

	@JsonProperty("marketplace_publisher_id")
	protected Integer marketplacePublisherAccountId;

	@JsonProperty("redpanda_publisher_creator_id")
	protected Integer redPandaPublisherCreatorId;

	@JsonProperty("site_count")
	protected Integer currentSiteCount;

	@JsonProperty("default_product_format")
	private ProductFormat defaultProductFormat;

	@JsonProperty("parent_default_product_format")
	private ProductFormat parentDefaultProductFormat;

	/* Note: default doesn't necessary have the same format as default publisher's product ids,
	since they are used for different things. However, it probably is a good idea they match :-)
	One is user-defined, the other is created automatically. Todo?
	 */

	@JsonProperty("is_default")
	private boolean isDefault;

	@JsonProperty("passback_display_tag_html")
	private String passbackDisplayTagHtml;


	public PublisherWebModel()
	{
	}


	public PublisherWebModel(AccountPublisher account, AccountPublisher redPandaPublisherCreatorId, int siteCount)
	{
		super(account);

		if(account.getMarketplacePublisher() != null){
			setMarketplacePublisherAccountId(account.getMarketplacePublisher().getId());
		}

		setPassbackDisplayTagHtml(account.getPassbackDisplayTagHtml());


		if (redPandaPublisherCreatorId == null)
			setRedPandaPublisherCreatorId(null);
		else
			setRedPandaPublisherCreatorId(redPandaPublisherCreatorId.getId());
		setCurrentSiteCount(siteCount);
		setDefaultProductFormat(account.getDefaultProductFormat());
		setDefault(account.isDefault());
		setParentDefaultProductFormat(null);
		if (account.getParentAccount() != null && account.getDefaultProductFormat() == null)
		{
			setParentDefaultProductFormat(((AccountNetwork)account.getParentAccount()).getDefaultProductFormat()); // Assumed parent is always network
		}
	}

	public PublisherWebModel(PublisherWebModel p)
	{
		super(p);
		this.setMarketplacePublisherAccountId(p.getMarketplacePublisherAccountId());
		this.setRedPandaPublisherCreatorId( p.getRedPandaPublisherCreatorId());
		this.setCurrentSiteCount(p.getCurrentSiteCount());
		this.setDefaultProductFormat(p.getDefaultProductFormat());
		this.setDefault(p.isDefault());
		this.setParentDefaultProductFormat(p.getParentDefaultProductFormat());
		this.setPassbackDisplayTagHtml(p.getPassbackDisplayTagHtml());
	}

	public Integer getMarketplacePublisherAccountId()
	{
		return marketplacePublisherAccountId;
	}

	public void setMarketplacePublisherAccountId(Integer marketplacePublisherAccountId)
	{
		this.marketplacePublisherAccountId = marketplacePublisherAccountId;
	}

	public Integer getRedPandaPublisherCreatorId()
	{
		return redPandaPublisherCreatorId;
	}

	// This is only set internally only for returning values from API calls. Usually should set marketplacePublisherAccountId instead for sending to services
	public void setRedPandaPublisherCreatorId(Integer redPandaPublisherCreatorId)
	{
		this.redPandaPublisherCreatorId = redPandaPublisherCreatorId;
	}

	public Integer getCurrentSiteCount()
	{
		return currentSiteCount;
	}

	public void setCurrentSiteCount(Integer currentSiteCount)
	{
		this.currentSiteCount = currentSiteCount;
	}


	public ProductFormat getDefaultProductFormat()
	{
		return defaultProductFormat;
	}

	public void setDefaultProductFormat(ProductFormat defaultProductFormat)
	{
		this.defaultProductFormat = defaultProductFormat;
	}

	public boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(boolean aDefault)
	{
		isDefault = aDefault;
	}

	public ProductFormat getParentDefaultProductFormat()
	{
		return parentDefaultProductFormat;
	}

	public void setParentDefaultProductFormat(ProductFormat parentDefaultProductFormat)
	{
		this.parentDefaultProductFormat = parentDefaultProductFormat;
	}

	public String getPassbackDisplayTagHtml()
	{
		return passbackDisplayTagHtml;
	}

	public void setPassbackDisplayTagHtml(String passbackDisplayTagHtml)
	{
		this.passbackDisplayTagHtml = passbackDisplayTagHtml;
	}
}
