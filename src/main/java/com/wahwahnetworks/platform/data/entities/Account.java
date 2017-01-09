package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="type")
@Table(name = "accounts")
public abstract class Account
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "account_notes")
	private String accountNotes;

	@Column(name = "internal_notes")
	private String internalNotes;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "language")
	@Enumerated(EnumType.STRING)
	private Language language;

	@Column(name = "country")
	@Enumerated(EnumType.STRING)
	private Country country;

	@Column(name = "is_archived")
	private boolean isArchived;

	@ManyToOne
	@JoinColumn(name = "parent_account_id")
	private Account parentAccount;

	@OneToMany(mappedBy = "parentAccount")
	private List<Account> childAccounts;

	public Account()
	{
		isArchived = false;
        childAccounts = new ArrayList<>();
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public AccountType getAccountType(){

		if(this instanceof AccountFree){
			return AccountType.FREE;
		}

		if(this instanceof AccountPublisher){
			return AccountType.PUBLISHER;
		}

		if(this instanceof AccountNetwork){
			return AccountType.NETWORK;
		}

		if(this instanceof AccountReportingPro){
			return AccountType.REPORTING_PRO;
		}

		throw new RuntimeException("Unknown account type");
	}

	public String getAccountNotes()
	{
		return accountNotes;
	}

	public void setAccountNotes(String accountNotes)
	{
		this.accountNotes = accountNotes;
	}

	public String getInternalNotes()
	{
		return internalNotes;
	}

	public void setInternalNotes(String internalNotes)
	{
		this.internalNotes = internalNotes;
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

	public Country getCountry()
	{
		return country;
	}

	public void setCountry(Country country)
	{
		this.country = country;
	}

	public Account getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(Account parentAccount) {
		this.parentAccount = parentAccount;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}

    public List<Account> getChildAccounts() {
        return childAccounts;
    }

    public void setChildAccounts(List<Account> childAccounts) {
        this.childAccounts = childAccounts;
    }
}
