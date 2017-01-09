package com.wahwahnetworks.platform.models.generation;

import com.wahwahnetworks.platform.models.web.ProductClientFeatureModel;

import java.util.Map;

/**
 * Created by Brian.Bober on 7/14/2015.
 */
public class GenerationTagModel
{
	private String productTypeDescription;
	private String productName;
	private String publisherName;
	private String networkName;
	private String siteName;
	private Integer productId;
	private Integer widgetId;
	private String objectPath;
	private String instructionsPagePosition;
	private String defaultTagFormat; // Only for universal tags
	private Integer objectId; // Only for universal tags; id attribute

	private Map<String,ProductClientFeatureModel> clientFeatureModelMap;

	public String getProductTypeDescription()
	{
		return productTypeDescription;
	}

	public void setProductTypeDescription(String productTypeDescription)
	{
		this.productTypeDescription = productTypeDescription;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public Integer getWidgetId()
	{
		return widgetId;
	}

	public void setWidgetId(Integer widgetId)
	{
		this.widgetId = widgetId;
	}

	public String getObjectPath()
	{
		return objectPath;
	}

	public void setObjectPath(String objectPath)
	{
		this.objectPath = objectPath;
	}

	public String getPublisherName()
	{
		return publisherName;
	}

	public void setPublisherName(String publisherName)
	{
		this.publisherName = publisherName;
	}

	public String getInstructionsPagePosition()
	{
		return instructionsPagePosition;
	}

	public void setInstructionsPagePosition(String instructionsPagePosition)
	{
		this.instructionsPagePosition = instructionsPagePosition;
	}

	public Map<String, ProductClientFeatureModel> getClientFeatures() {
		return clientFeatureModelMap;
	}

	public void setClientFeatures(Map<String, ProductClientFeatureModel> clientFeatureModelMap) {
		this.clientFeatureModelMap = clientFeatureModelMap;
	}

	public String getNetworkName()
	{
		return networkName;
	}

	public void setNetworkName(String networkName)
	{
		this.networkName = networkName;
	}

	public String getDefaultTagFormat()
	{
		return defaultTagFormat;
	}

	public void setDefaultTagFormat(String defaultTagFormat)
	{
		this.defaultTagFormat = defaultTagFormat;
	}

	public Integer getObjectId()
	{
		return objectId;
	}

	public void setObjectId(Integer objectId)
	{
		this.objectId = objectId;
	}
}
