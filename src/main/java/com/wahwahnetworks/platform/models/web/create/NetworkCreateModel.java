package com.wahwahnetworks.platform.models.web.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;

import javax.validation.constraints.NotNull;

@WebSafeModel
public class NetworkCreateModel
{

	@JsonProperty("name")
	protected String name;

	@JsonProperty("autoCreateNetworkPublisher")
	protected Boolean autoCreateNetworkPublisher;

	@JsonProperty("language")
	@NotNull
	private Language language;

	@JsonProperty("country")
	@NotNull
	private Country country;

	@JsonProperty("account_type")
	@NotNull
	private AccountType accountType;

	protected NetworkCreateModel()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public Boolean getAutoCreateNetworkPublisher()
	{
		return autoCreateNetworkPublisher;
	}

	public void setAutoCreateNetworkPublisher(Boolean autoCreateNetworkPublisher)
	{
		this.autoCreateNetworkPublisher = autoCreateNetworkPublisher;
	}
}
