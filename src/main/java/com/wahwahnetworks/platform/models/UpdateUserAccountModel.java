package com.wahwahnetworks.platform.models;

/**
 * Created by Justin on 6/9/2014.
 */
public class UpdateUserAccountModel
{
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String accountName;
	private Boolean loginEnabled;


	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public Boolean isLoginEnabled()
	{
		return loginEnabled;
	}

	public void setLoginEnabled(Boolean loginEnabled)
	{
		this.loginEnabled = loginEnabled;
	}
}
