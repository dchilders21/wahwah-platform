package com.wahwahnetworks.platform.models.web.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@WebSafeModel
public class SiteCreateModel
{
	@JsonProperty("publisher_id")
	private int publisherId;

	@JsonProperty("site_name")
	@NotBlank
	@Size(max = 255)
	private String siteName;

	@JsonProperty("site_url")
	@NotBlank
	@Size(max = 255)
	private String siteUrl;

	@JsonProperty("site_language")
	@NotNull
	private Language language;

	@JsonProperty("site_country")
	@NotNull
	private Country country;

	@JsonProperty("custom_site")
	@NotNull
	private boolean customSite;

	public SiteCreateModel()
	{
	}

	public SiteCreateModel(SiteCreateModel s)
	{
		this.setSiteName(s.getSiteName());
		this.setCountry(s.getCountry());
		this.setPublisherId(s.getPublisherId());
		this.setCustomSite(s.isCustomSite());
		this.setLanguage(s.getLanguage());
		this.setSiteUrl(s.getSiteUrl());
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
		if (siteUrl == null)
			return "";
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

	public boolean isCustomSite()
	{
		return customSite;
	}

	public void setCustomSite(boolean customSite)
	{
		this.customSite = customSite;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage(Language language)
	{
		this.language = language;
	}


	public Country getCountry()
	{
		return country;
	}

	public void setCountry(Country country)
	{
		this.country = country;
	}

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
}
