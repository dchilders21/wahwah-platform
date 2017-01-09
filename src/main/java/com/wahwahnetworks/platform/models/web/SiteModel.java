package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.*;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@WebSafeModel
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
		@JsonSubTypes.Type(value = SiteInternalModel.class, name = "InternalSite")
)
public class SiteModel
{
	private static final Logger log = Logger.getLogger(SiteModel.class);

	@JsonProperty("id")
	private int id;

	@JsonProperty("account_id")
	private int accountId;

	@JsonProperty("site_name")
	@NotBlank
	@Size(max = 255)
	private String siteName;

	@JsonProperty("site_url")
	@NotBlank
	@Size(max = 255)
	private String siteUrl;

	@JsonProperty("contact_name")
	@Size(max = 255)
	private String contactName;

	@JsonProperty("contact_email")
	@Email
	@Size(max = 255)
	private String contactEmail;

	@JsonProperty("site_language")
	@NotNull
	private Language language;

	@JsonProperty("site_type")
	private SiteType siteType;

	@JsonProperty("site_notes")
	private String siteNotes;

	@JsonProperty("traffic_estimate")
	private TrafficEstimate trafficEstimate;

	@JsonProperty("site_country")
	private Country siteCountry;

	@JsonProperty("account_name")
	private String accountName;

	@JsonProperty("inherit_pub_details")
	private Boolean inheritPubDetails;

	@JsonProperty("is_live")
	private Boolean isLive;

	@JsonProperty("is_archived")
	private Boolean isArchived;

	@JsonProperty("marketplace_linked_site_id")
	private Integer marketplaceSiteId;

	@JsonProperty("redpanda_linked_site_id")
	private Integer redPandaSiteCreatorId;

	@JsonProperty("is_default")
	private boolean isDefault;

	@JsonProperty("default_product_id")
	private Integer defaultProductId;

	@JsonProperty("passback_display_tag_html")
	private String passbackDisplayTagHtml;

	/* Note: default product doesn't necessary have the same format as default product id,
	since they are used for different things. However, it probably is a good idea they match :-)
	One is user-defined, the other is created automatically. Todo?
	 */
	@JsonProperty("default_product_format")
	private ProductFormat defaultProductFormat;


	public SiteModel(Site site)
	{
		this.setId(site.getId());
		this.setSiteName(site.getSiteName());
		this.setSiteUrl(site.getSiteUrl());
		this.setContactName(site.getContactName());
		this.setContactEmail(site.getContactEmail());
		this.setLanguage(site.getLanguage());
		this.setSiteType(site.getSiteType());
		this.setSiteNotes(site.getSiteNotes());
		this.setSiteCountry(site.getSiteCountry());
		this.setTrafficEstimate(site.getTrafficEstimate());
		this.setIsInheritPubDetails(site.isInheritPubDetails());
		this.setAccountId(site.getPublisher().getId());
		this.setAccountName(site.getPublisher().getName());
		this.setLive(site.isLive());
		this.setArchived(site.isArchived());
		this.setDefault(site.isDefault());
		this.setPassbackDisplayTagHtml(site.getPassbackDisplayTagHtml());

		if(site.getDefaultProduct() != null) {
			this.setDefaultProductId(site.getDefaultProduct().getId());
		}

		this.setDefaultProductFormat(site.getDefaultProductFormat());
		if (site.getMarketplaceSite() != null)
		{
			this.setMarketplaceSiteId(site.getMarketplaceSite().getId());
		}
	}

	public SiteModel()
	{
	}

	public SiteModel(SiteModel s)
	{
		this.setId(s.getId());
		this.setSiteName(s.getSiteName());
		this.setSiteUrl(s.getSiteUrl());
		this.setContactName(s.getContactName());
		this.setContactEmail(s.getContactEmail());
		this.setLanguage(s.getLanguage());
		this.setSiteType(s.getSiteType());
		this.setSiteNotes(s.getSiteNotes());
		this.setSiteCountry(s.getSiteCountry());
		this.setTrafficEstimate(s.getTrafficEstimate());
		this.setIsInheritPubDetails(s.isInheritPubDetails());
		this.setAccountId(s.getAccountId());
		this.setAccountName(s.getAccountName());
		this.setLive(s.getLive());
		this.setArchived(s.getArchived());
		this.setMarketplaceSiteId(s.getMarketplaceSiteId());
		this.setDefault(s.isDefault());
		this.setDefaultProductId(s.getDefaultProductId());
		this.setDefaultProductFormat(s.getDefaultProductFormat());
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getSiteUrl()
	{
		if (!siteUrl.startsWith("http://") && !siteUrl.startsWith("https://"))
		{
			siteUrl = "http://" + siteUrl;
		}

		return siteUrl;
	}

	public void setSiteUrl(String siteUrl)
	{
		this.siteUrl = siteUrl;
	}

	public String getContactName()
	{
		return contactName;
	}

	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}

	public String getContactEmail()
	{
		return contactEmail;
	}

	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage(Language language)
	{
		this.language = language;
	}

	public SiteType getSiteType()
	{
		return siteType;
	}

	public void setSiteType(SiteType siteType)
	{
		this.siteType = siteType;
	}


	public String getSiteNotes()
	{
		return siteNotes;
	}

	public void setSiteNotes(String siteNotes)
	{
		this.siteNotes = siteNotes;
	}


	public TrafficEstimate getTrafficEstimate()
	{
		return trafficEstimate;
	}

	public void setTrafficEstimate(TrafficEstimate trafficEstimate)
	{
		this.trafficEstimate = trafficEstimate;
	}

	public Country getSiteCountry()
	{
		return siteCountry;
	}

	public void setSiteCountry(Country country)
	{
		this.siteCountry = country;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public Boolean isInheritPubDetails()
	{
		return inheritPubDetails;
	}

	public void setIsInheritPubDetails(Boolean isInheritPubDetails)
	{
		this.inheritPubDetails = isInheritPubDetails;
	}

	public Boolean getLive()
	{
		return isLive;
	}

	public void setLive(Boolean live)
	{
		isLive = live;
	}

	public Boolean getArchived()
	{
		return isArchived;
	}

	public void setArchived(Boolean archived)
	{
		isArchived = archived;
	}

	public Boolean getInheritPubDetails()
	{
		return inheritPubDetails;
	}

	public void setInheritPubDetails(Boolean inheritPubDetails)
	{
		this.inheritPubDetails = inheritPubDetails;
	}

	public Integer getMarketplaceSiteId()
	{
		return marketplaceSiteId;
	}

	public void setMarketplaceSiteId(Integer marketplaceSiteId)
	{
		this.marketplaceSiteId = marketplaceSiteId;
	}

	public Integer getRedPandaSiteCreatorId()
	{
		return redPandaSiteCreatorId;
	}

	public void setRedPandaSiteCreatorId(Integer redPandaSiteCreatorId)
	{
		this.redPandaSiteCreatorId = redPandaSiteCreatorId;
	}

	public boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(boolean aDefault)
	{
		isDefault = aDefault;
	}

	public Integer getDefaultProductId()
	{
		return defaultProductId;
	}

	public void setDefaultProductId(Integer defaultProductId)
	{
		this.defaultProductId = defaultProductId;
	}

	public ProductFormat getDefaultProductFormat()
	{
		return defaultProductFormat;
	}

	public void setDefaultProductFormat(ProductFormat defaultProductFormat)
	{
		this.defaultProductFormat = defaultProductFormat;
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
