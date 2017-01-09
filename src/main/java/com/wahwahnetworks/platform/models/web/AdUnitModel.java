package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AdUnit;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.entities.enums.AdUnitType;

/**
 * Created by Brian.Bober on 5/12/2015.
 */

@WebSafeModel
public class AdUnitModel
{
	public AdUnitModel()
	{

	}

	public void fromAdUnitAndProduct(AdUnit unit, ProductModel productModel)
	{
		this.adServerType = unit.getAdServerType();
		this.adServerUnitId = unit.getAdServerUnitId();
		this.platformCreated = unit.isPlatformCreated();
		this.width = unit.getWidth();
		this.height = unit.getHeight();
		this.adUnitType = unit.getAdUnitType();
		this.productId = unit.getProduct().getId();
		this.id = unit.getId();
		this.title = unit.getTitle();
        this.adUnitName = this.title;
		this.isArchived = unit.isArchived();

        AdConfigInPageModel adConfigInPage = productModel.getAdConfigInPage();

		if(adConfigInPage != null) {
			if (adConfigInPage.getPrimaryDisplayAdUnitId() == id) {
				adUnitName = "First Look Display";
			}

			if (adConfigInPage.getBackupDisplayAdUnitId() == id) {
				adUnitName = "Backup Display";
			}

			if (adConfigInPage.getVideoAdUnitId() == id) {
				adUnitName = "Video";
			}

			if (adConfigInPage.getMobileAdUnitId() == id) {
				adUnitName = "Mobile";
			}

			if (adConfigInPage.getTabletAdUnitId() == id) {
				adUnitName = "Tablet";
			}

			if (adConfigInPage.getLeaveBehindAdUnitId() == id) {
				adUnitName = "Leave Behind";
			}
		}
	}

	@JsonProperty("id")
	private int id;

	@JsonProperty("ad_server")
	private AdServerType adServerType;

	@JsonProperty("external_id")
	private String adServerUnitId;

	@JsonProperty("platform_created")
	private Boolean platformCreated;

	@JsonProperty("width")
	private int width;

	@JsonProperty("height")
	private int height;

	@JsonProperty("unit_type")
	private AdUnitType adUnitType;

	@JsonProperty("product_id")
	private int productId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("ad_unit_name")
	private String adUnitName;

	@JsonProperty("is_archived")
	private Boolean isArchived;

	public AdServerType getAdServerType()
	{
		return adServerType;
	}

	public void setAdServerType(AdServerType adServerType)
	{
		this.adServerType = adServerType;
	}

	public String getAdServerUnitId()
	{
		return adServerUnitId;
	}

	public void setAdServerUnitId(String adServerUnitId)
	{
		this.adServerUnitId = adServerUnitId;
	}

	public Boolean isPlatformCreated()
	{
		return platformCreated;
	}

	public void setPlatformCreated(Boolean platformCreated)
	{
		this.platformCreated = platformCreated;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public AdUnitType getAdUnitType()
	{
		return adUnitType;
	}

	public void setAdUnitType(AdUnitType adUnitType)
	{
		this.adUnitType = adUnitType;
	}

	public int getProductId()
	{
		return productId;
	}

	public void setProductId(int productId)
	{
		this.productId = productId;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

    public String getAdUnitName() {
        return adUnitName;
    }

    public void setAdUnitName(String adUnitName) {
        this.adUnitName = adUnitName;
    }

	public Boolean getArchived()
	{
		return isArchived;
	}

	public void setArchived(Boolean archived)
	{
		isArchived = archived;
	}
}
