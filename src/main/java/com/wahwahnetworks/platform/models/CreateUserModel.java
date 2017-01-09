package com.wahwahnetworks.platform.models;

import java.util.List;

/**
 * Created by Justin on 6/9/2014.
 */
public class CreateUserModel
{
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String accountName;
	private List<String> roles;

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

	public void setAccountName(String publisherName)
	{
		this.accountName = publisherName;
	}

	public List<String> getRoles()
	{
		return roles;
	}

	public void setRoles(List<String> roles)
	{
		this.roles = roles;
	}
}
