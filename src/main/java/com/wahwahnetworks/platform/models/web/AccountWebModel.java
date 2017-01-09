package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Justin.Haygood on 1/29/2016.
 */

@WebSafeModel
public class AccountWebModel
{
	@JsonProperty("id")
	protected Integer accountId;

	@JsonProperty("name")
	protected String name;

	@JsonProperty("account_notes")
	protected String accountNotes;

	@JsonProperty("language")
	@NotNull
	private Language language;

	@JsonProperty("country")
	private Country country;

	@JsonProperty("contact_name")
	@Size(max = 255)
	private String contactName;

	@JsonProperty("contact_email")
	@Email
	@Size(max = 255)
	private String contactEmail;

	@JsonProperty("parent_account_id")
	private Integer parentAccountId;

	@JsonProperty("parent_account_type")
	private AccountType parentAccountType;

	@JsonProperty("account_type")
	private AccountType accountType;

	@JsonProperty("is_archived")
	private Boolean isArchived;

	protected AccountWebModel()
	{
	}

	public AccountWebModel(Account account)
	{
		setAccountId(account.getId());
		setName(account.getName());
		setAccountNotes(account.getAccountNotes());
		setContactEmail(account.getContactEmail());
		setContactName(account.getContactName());
		setLanguage(account.getLanguage());
		setCountry(account.getCountry());
		setAccountType(account.getAccountType());
		setArchived(account.isArchived());
		if (account.getParentAccount() != null){
			setParentAccountId(account.getParentAccount().getId());
			setParentAccountType(account.getParentAccount().getAccountType());
		} else {
			setParentAccountId(null);
			setParentAccountType(null);
		}

	}

	public AccountWebModel(AccountWebModel a)
	{
		this.setAccountId(a.getAccountId());
		this.setName(a.getName());
		this.setAccountNotes(a.getAccountNotes());
		this.setLanguage(a.getLanguage());
		this.setCountry(a.getCountry());
		this.setContactName(a.getContactName());
		this.setContactEmail(a.getContactEmail());
		this.setParentAccountId(a.getParentAccountId());
		this.setAccountType(a.getAccountType());
		this.setArchived(a.getArchived());
	}

	public static AccountWebModel getRootAccount(){
		AccountWebModel account = new AccountWebModel();
		account.setName("Red Panda Marketplace");
		account.setAccountType(AccountType.ROOT);
		account.setLanguage(Language.en);
		account.setCountry(Country.UNITED_STATES);
		account.setArchived(false);
		return account;
	}

	public Integer getAccountId()
	{
		return accountId;
	}

	public void setAccountId(Integer accountId)
	{
		this.accountId = accountId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	public String getAccountNotes()
	{
		return accountNotes;
	}

	public void setAccountNotes(String accountNotes)
	{
		this.accountNotes = accountNotes;
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

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Integer getParentAccountId()
	{
		return parentAccountId;
	}

	public void setParentAccountId(Integer parentAccountId)
	{
		this.parentAccountId = parentAccountId;
	}

	public Boolean getArchived()
	{
		return isArchived;
	}

	public void setArchived(Boolean archived)
	{
		isArchived = archived;
	}

	public AccountType getParentAccountType() {
		return parentAccountType;
	}

	public void setParentAccountType(AccountType parentAccountType) {
		this.parentAccountType = parentAccountType;
	}
}
