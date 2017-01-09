package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.entities.enums.AdUnitType;
import com.wahwahnetworks.platform.models.web.AdUnitModel;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/9/15.
 */

@Entity
@Table(name = "ad_units")
public class AdUnit
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "ad_server")
	@Enumerated(EnumType.STRING)
	private AdServerType adServerType;

	@Column(name = "external_id")
	private String adServerUnitId;  // Could be a url for AdServerType.GENERIC_URL, otherwise, most likely an Openx ID

	@Column(name = "external_id2")
	private String adServerUnitId2;  // Openx requires uuid for future PUT operations even though UID is deprecated :-/

	@Column(name = "platform_created") // Required unit or not?
	private Boolean platformCreated;

	@Column(name = "width")
	private int width;

	@Column(name = "height")
	private int height;

	@Column(name = "unit_type")
	@Enumerated(EnumType.STRING)
	private AdUnitType adUnitType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "title")
	private String title;

	@Column(name = "is_archived")
	private boolean isArchived;

	public AdUnit()
	{
		isArchived = false;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public AdServerType getAdServerType()
	{
		return adServerType;
	}

	public void setAdServerType(AdServerType adServerType)
	{
		this.adServerType = adServerType;
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

	public Product getProduct()
	{
		return product;
	}

	public void setProduct(Product product)
	{
		this.product = product;
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

	public String getAdServerUnitId2()
	{
		return adServerUnitId2;
	}

	public void setAdServerUnitId2(String adServerUnitId2)
	{
		this.adServerUnitId2 = adServerUnitId2;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void fromAdUnitModel(AdUnitModel unitModel, Product product)
	{
		this.adServerType = unitModel.getAdServerType();
		this.adServerUnitId = unitModel.getAdServerUnitId();
		this.platformCreated = unitModel.isPlatformCreated();
		this.width = unitModel.getWidth();
		this.height = unitModel.getHeight();
		this.adUnitType = unitModel.getAdUnitType();
		this.id = unitModel.getId();
		this.title = unitModel.getTitle();
		this.isArchived = product.isArchived();
		this.product = product;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}
}
