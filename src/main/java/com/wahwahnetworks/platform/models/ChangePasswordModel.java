package com.wahwahnetworks.platform.models;

/**
 * Created by Justin on 5/15/2014.
 */
public class ChangePasswordModel
{
	private String password;
	private String confirmPassword;

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getConfirmPassword()
	{
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	public boolean doPasswordsMatch()
	{
		return password.equals(confirmPassword);
	}
}
