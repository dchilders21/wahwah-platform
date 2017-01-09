package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ProductType;
import com.wahwahnetworks.platform.data.entities.enums.WWLogLevel;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 12/21/2014.
 */


@Entity
@Table(name = "products")
public class Product
{

	private static final Logger log = Logger.getLogger(Product.class);

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private ProductType productType;

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProductToolbar productToolbar;

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProductStandaloneAd productStandaloneAd;

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private AdConfigInPage adConfigInPage;

	@ManyToOne
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductClientFeature> features;

	@Column(name = "log_level")
	@Enumerated(EnumType.STRING)
	private WWLogLevel logLevel;

	@Column(name = "product_name")
	private String name;

	@Column(name = "debug_mode")
	private boolean isDebug = false;

	@Column(name = "widget_id")
	private Integer widgetId;

	@Column(name = "is_locked")
	private boolean isLocked = false;

	@Column(name = "is_archived")
	private boolean isArchived;

	@ManyToOne
	@JoinColumn(name = "locked_user_id", nullable = true)
	private User lockedUser;

	@Column(name = "is_default")
	private boolean isDefault;

	public Product()
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

	public Site getSite()
	{
		return site;
	}

	public void setSite(Site site)
	{
		this.site = site;
	}

	public ProductType getProductType()
	{
		return productType;
	}

	public void setProductType(ProductType productType)
	{
		this.productType = productType;
	}

	public ProductToolbar getProductToolbar()
	{
		return productToolbar;
	}

	public void setProductToolbar(ProductToolbar productToolbar)
	{
		this.productToolbar = productToolbar;
	}


	public ProductStandaloneAd getProductStandaloneAd()
	{
		return productStandaloneAd;
	}

	public void setProductStandaloneAd(ProductStandaloneAd productStandaloneAd)
	{
		this.productStandaloneAd = productStandaloneAd;
	}

	public WWLogLevel getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(WWLogLevel logLevel)
	{
		this.logLevel = logLevel;
	}

	public boolean getIsDebug()
	{
		return isDebug;
	}

	public void setIsDebug(boolean isDebug)
	{
		this.isDebug = isDebug;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public AdConfigInPage getAdConfigInPage()
	{
		return adConfigInPage;
	}

	public void setAdConfigInPage(AdConfigInPage adConfigInPage)
	{
		this.adConfigInPage = adConfigInPage;
	}


	public ProductVersion getProductVersion()
	{
		return productVersion;
	}

	public void setProductVersion(ProductVersion productVersion)
	{
		this.productVersion = productVersion;
	}

	public boolean getLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public User getLockedUser() {
		return lockedUser;
	}

	public void setLockedUser(User lockedUser) {
		this.lockedUser = lockedUser;
	}

	public List<ProductClientFeature> getFeatures() {

		if(features == null){
			features = new ArrayList<>();
		}

		return features;
	}

	public void setFeatures(List<ProductClientFeature> features) {
		this.features = features;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}

	public boolean isDebug()
	{
		return isDebug;
	}

	public void setDebug(boolean debug)
	{
		isDebug = debug;
	}

	public boolean isLocked()
	{
		return isLocked;
	}

	public boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(boolean aDefault)
	{
		isDefault = aDefault;
	}

	public Integer getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Integer widgetId) {
		this.widgetId = widgetId;
	}
}
