package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Product;
import com.wahwahnetworks.platform.data.entities.ProductClientFeature;
import com.wahwahnetworks.platform.data.entities.ProductVersion;
import com.wahwahnetworks.platform.data.entities.RevenueModel;
import com.wahwahnetworks.platform.data.entities.enums.ProductType;
import com.wahwahnetworks.platform.data.entities.enums.WWLogLevel;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Brian.Bober on 12/23/2014.
 */


@WebSafeModel
public class ProductModel
{

	@JsonProperty("id")
	private int id;

	@JsonProperty("toolbar")
	private ProductToolbarModel toolbar;

	@JsonProperty("standalone_ad")
    @NotNull
	private ProductStandaloneAdModel standaloneAd;

	@JsonProperty("site_id")
	private int site;

	@JsonProperty("site_account_id")
	private int siteAccountId;

    @JsonProperty("widget_id")
    private Integer widgetId;

	@JsonProperty("type")
	private ProductType productType;

	@JsonProperty("log_level")
	private WWLogLevel logLevel;

	@JsonProperty("debug_mode")
	private Boolean isDebug;

	@JsonProperty("product_name")
	private String name;

	@JsonProperty("product_version_id")
	private ProductVersion productVersion;

	@JsonProperty("is_locked")
	private Boolean isLocked;

	@JsonProperty("locked_user_id")
	private Integer lockedUserId;

	@JsonProperty("features")
	private List<ProductClientFeatureModel> features;

	@JsonProperty("is_archived")
	private Boolean isArchived;

    @JsonProperty("adconfig_inpage")
    @NotNull
    private AdConfigInPageModel adConfigInPage;


	@JsonProperty("is_default")
	private boolean isDefault;

	public ProductModel()
	{

	}

	public ProductModel(Product product, RevenueModel revenueModel)
	{
		this.setId(product.getId());
		this.site = product.getSite().getId();
		this.siteAccountId = product.getSite().getPublisher().getId();
        this.widgetId = product.getWidgetId();

		this.productType = product.getProductType();

        if (product.getProductType() == ProductType.TOOLBAR || product.getProductType() == ProductType.MINI_BAR || product.getProductType() == ProductType.CUSTOM)
		{
			toolbar = new ProductToolbarModel(product.getProductToolbar());

		}
		else if (product.getProductType() == ProductType.STANDALONE_AD)
		{
			standaloneAd = new ProductStandaloneAdModel(product.getProductStandaloneAd());
		}

		this.name = product.getName();
		this.isDebug = product.getIsDebug();
		this.logLevel = product.getLogLevel();
		this.isLocked = product.getLocked();
		this.productVersion = product.getProductVersion();
		this.isDefault = product.isDefault();

		if (product.getLockedUser() != null) {
			this.lockedUserId = product.getLockedUser().getId();
		}

		this.setFeatures(new ArrayList<>());
		this.setArchived(product.isArchived());

        this.setAdConfigInPage(new AdConfigInPageModel(product.getAdConfigInPage()));

		for (ProductClientFeature productClientFeature : product.getFeatures()){

			ClientFeatureWebModel clientFeatureWebModel = new ClientFeatureWebModel();
			clientFeatureWebModel.setName(productClientFeature.getClientFeature().getName());
			clientFeatureWebModel.setDescription(productClientFeature.getClientFeature().getDescription());
			clientFeatureWebModel.setId(productClientFeature.getClientFeature().getId());
			clientFeatureWebModel.setVariableLabel(productClientFeature.getClientFeature().getClientVariableLabel());
			clientFeatureWebModel.setVariableName(productClientFeature.getClientFeature().getClientVariableName());
			clientFeatureWebModel.setVariableType(productClientFeature.getClientFeature().getClientVariableType());

			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());
			productClientFeatureModel.setClientFeature(clientFeatureWebModel);
			getFeatures().add(productClientFeatureModel);
		}
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public ProductToolbarModel getToolbar()
	{
		return toolbar;
	}

	public void setToolbar(ProductToolbarModel toolbar)
	{
		this.toolbar = toolbar;
	}

	public int getSite()
	{
		return site;
	}

	public void setSite(int site)
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

	public ProductStandaloneAdModel getStandaloneAd()
	{
		return standaloneAd;
	}

	public void setStandaloneAd(ProductStandaloneAdModel standaloneAd)
	{
		this.standaloneAd = standaloneAd;
	}

	public WWLogLevel getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(WWLogLevel logLevel)
	{
		this.logLevel = logLevel;
	}

	public Boolean getIsDebug()
	{
		return isDebug;
	}

	public void setIsDebug(Boolean isDebug)
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

	public ProductVersion getProductVersion()
	{
		return productVersion;
	}

	public void setProductVersion(ProductVersion productVersion)
	{
		this.productVersion = productVersion;
	}

	public int getSiteAccountId()
	{
		return siteAccountId;
	}

	public void setSiteAccountId(int siteAccountId)
	{
		this.siteAccountId = siteAccountId;
	}

	public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public Integer getLockedUserId() {
		return lockedUserId;
	}

	public void setLockedUserId(Integer lockedUserId) {
		this.lockedUserId = lockedUserId;
	}

	public List<ProductClientFeatureModel> getFeatures() {
		return features;
	}

	public void setFeatures(List<ProductClientFeatureModel> features) {
		this.features = features;
	}

	public Boolean getArchived()
	{
		return isArchived;
	}

	public void setArchived(Boolean archived)
	{
		isArchived = archived;
	}

	public Boolean getDebug()
	{
		return isDebug;
	}

	public void setDebug(Boolean debug)
	{
		isDebug = debug;
	}

	public Boolean getLocked()
	{
		return isLocked;
	}

	public void setLocked(Boolean locked)
	{
		isLocked = locked;
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

    public AdConfigInPageModel getAdConfigInPage() {
        return adConfigInPage;
    }

    public void setAdConfigInPage(AdConfigInPageModel adConfigInPage) {
        this.adConfigInPage = adConfigInPage;
    }
}