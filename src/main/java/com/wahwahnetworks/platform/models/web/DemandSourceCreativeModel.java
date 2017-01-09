package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.enums.CreativePlatform;
import com.wahwahnetworks.platform.data.entities.enums.CreativeType;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@WebSafeModel
public class DemandSourceCreativeModel
{
	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("width")
	private Integer width;

	@JsonProperty("height")
	private Integer height;

	@JsonProperty("content_type")
	private CreativeType contentType;

	@JsonProperty("vendor_type")
	private String vendorType;

	@JsonProperty("content_url")
	private String contentUrl;

	@JsonProperty("content_snippet")
	private String contentSnippet;

	@JsonProperty("creative_platform")
	private CreativePlatform creativePlatform;

    public DemandSourceCreativeModel(){
    }

    public DemandSourceCreativeModel(Creative creative){
        setId(creative.getId());
        setName(creative.getName());
        setContentType(creative.getCreativeType());
        setVendorType(creative.getVendorType());
		setCreativePlatform(creative.getCreativePlatform());

        switch(contentType){
            case HTML:
                setContentSnippet(creative.getTagContents());
                break;
            case VAST_XML:
                setContentUrl(creative.getTagContents());
                break;
        }
    }

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getWidth()
	{
		return width;
	}

	public void setWidth(Integer width)
	{
		this.width = width;
	}

	public Integer getHeight()
	{
		return height;
	}

	public void setHeight(Integer height)
	{
		this.height = height;
	}

	public CreativeType getContentType()
	{
		return contentType;
	}

	public void setContentType(CreativeType contentType)
	{
		this.contentType = contentType;
	}

	public String getContentUrl()
	{
		return contentUrl;
	}

	public void setContentUrl(String contentUrl)
	{
		this.contentUrl = contentUrl;
	}

	public String getContentSnippet()
	{
		return contentSnippet;
	}

	public void setContentSnippet(String contentSnippet)
	{
		this.contentSnippet = contentSnippet;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getVendorType() {
        return vendorType;
    }

    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

	public CreativePlatform getCreativePlatform() {
		return creativePlatform;
	}

	public void setCreativePlatform(CreativePlatform creativePlatform) {
		this.creativePlatform = creativePlatform;
	}
}

