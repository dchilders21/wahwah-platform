package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.UserModel;

import java.util.Set;

/**
 * Created by Justin on 5/17/2014.
 */

@WebSafeModel
public class UserWebModel
{
	private int userId;
	private Integer accountId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String accountName;
	private AccountType accountType;
	private Set<UserRoleType> roles;
	private Boolean isActivated;
	private Boolean isLoginEnabled;
	private Boolean isSinglePublisherNetworkUser;
	private LogicalUserType userType;

	public UserWebModel(User user)
	{
		UserModel userModel = new UserModel(user);
		init(userModel);
	}

	public UserWebModel(UserModel userModel)
	{
		init(userModel);
	}

	private void init(UserModel userModel)
	{
		setUserId(userModel.getUserId());
		setAccountId(userModel.getAccountId());
		setAccountName(userModel.getAccountName());

		setAccountType(userModel.getAccountType());

		setFirstName(userModel.getFirstName());
		setLastName(userModel.getLastName());
		setEmailAddress(userModel.getEmailAddress());
		setRoles(userModel.getRoles());
		setIsLoginEnabled(userModel.getIsLoginEnabled());
		setIsActivated(userModel.getIsActivated());
		setSinglePublisherNetworkUser(userModel.getSinglePublisherNetworkUser());
        setUserType(userModel.getUserType());
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public Integer getAccountId()
	{
		return accountId;
	}

	public void setAccountId(Integer accountId)
	{
		this.accountId = accountId;
	}

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

	public Set<UserRoleType> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<UserRoleType> roles)
	{
		this.roles = roles;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public Boolean isActivated()
	{
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated)
	{
		this.isActivated = isActivated;
	}

	public Boolean isLoginEnabled()
	{
		return isLoginEnabled;
	}

	public void setIsLoginEnabled(Boolean isLoginEnabled)
	{
		this.isLoginEnabled = isLoginEnabled;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Boolean getSinglePublisherNetworkUser() {
		return isSinglePublisherNetworkUser;
	}

	public void setSinglePublisherNetworkUser(Boolean singlePublisherNetworkUser) {
		isSinglePublisherNetworkUser = singlePublisherNetworkUser;
	}

    public LogicalUserType getUserType() {
        return userType;
    }

    public void setUserType(LogicalUserType userType) {
        this.userType = userType;
    }
}
