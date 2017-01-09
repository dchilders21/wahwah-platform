package com.wahwahnetworks.platform.models.web.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;

import javax.validation.constraints.NotNull;

@WebSafeModel
public class PublisherCreateModel
{
	@JsonProperty("site")
	private SiteCreateModel site;

	@JsonProperty("name")
	protected String name;

	@JsonProperty("autoCreateSiteAndToolbar")
	protected Boolean autoCreateSiteAndToolbar;

	@JsonProperty("language")
	@NotNull
	private Language language;

	@JsonProperty("country")
	@NotNull
	private Country country;

	@JsonProperty("account_type")
	@NotNull
	private AccountType accountType;

	@JsonProperty("parent_account_id")
	private Integer networkId;

	public PublisherCreateModel()
	{
	}

	public PublisherCreateModel(PublisherCreateModel p)
	{
		this.setAccountType(p.getAccountType());
		this.setCountry(p.getCountry());
		this.setNetworkId(p.getNetworkId());
		this.setName(p.getName());
		this.setLanguage(p.getLanguage());
		this.setAutoCreateSiteAndToolbar(p.getAutoCreateSiteAndToolbar());
		this.setSite(p.getSite());
	}


	public SiteCreateModel getSite()
	{
		return site;
	}

	public void setSite(SiteCreateModel site)
	{
		this.site = site;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Boolean getAutoCreateSiteAndToolbar()
	{
		return autoCreateSiteAndToolbar;
	}

	public void setAutoCreateSiteAndToolbar(Boolean autoCreateSiteAndToolbar)
	{
		this.autoCreateSiteAndToolbar = autoCreateSiteAndToolbar;
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

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Integer getNetworkId()
	{
		return networkId;
	}

	public void setNetworkId(Integer networkId)
	{
		this.networkId = networkId;
	}
}
