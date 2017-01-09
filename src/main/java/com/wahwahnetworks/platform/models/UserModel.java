package com.wahwahnetworks.platform.models;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.web.LogicalUserType;
import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Justin on 5/12/2014.
 */
public class UserModel
{

	private static final Logger log = Logger.getLogger(UserModel.class);

	private int userId;
	private Integer accountId;
	private String accountName;

	private AccountType accountType;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private Boolean isActivated;

	private Boolean isSinglePublisherNetworkUser;

	private LogicalUserType userType;

	private Boolean isLoginEnabled;
	private Set<UserRoleType> roles;

	// Use to salt the activation key -- Merriam-Webster's word of the day on May 15, 2014.
	private static final String ACTIVATION_SALT = "shinplaster";

	// Use to salt the reset password key -- Dictionary.com's word of the day on May 15, 2014.
	private static final String RESET_SALT = "vane";

	// Use to salt the create password key -- Merriam-Webster's word of the day on June 9, 2014.
	private static final String CREATE_PASSWORD_SALT = "vane";

	// Use to salt the remember me key -- Merrian-Webster's word of the day on June 4, 2015
	private static final String REMEMBER_ME_SALT = "swan song";

	public UserModel()
	{

	}

	public UserModel(User user)
	{
		this.setUserId(user.getId());
        this.setSinglePublisherNetworkUser(false);

		Account account = user.getAccount();

		if (account != null)
		{
			this.setAccountId(account.getId());
			this.setAccountName(account.getName());
			this.setAccountType(account.getAccountType());

            if(account instanceof AccountNetwork){
                this.setSinglePublisherNetworkUser(((AccountNetwork)account).isSinglePublisher());
                setUserType(LogicalUserType.NETWORK_USER);
            }

            if(account instanceof AccountFree){
                setUserType(LogicalUserType.FREE_USER);
            }

            if(account instanceof AccountPublisher){
                AccountPublisher publisher = (AccountPublisher)account;

                if(publisher.getParentAccount() == null){
                    setUserType(LogicalUserType.MARKETPLACE_PUBLISHER_USER);
                } else {
                    setUserType(LogicalUserType.NETWORK_PUBLISHER_USER);
                }
            }

            if(account instanceof AccountReportingPro){
            	setUserType(LogicalUserType.REPORTING_PRO_USER);
			}

		} else {
			setAccountType(AccountType.ROOT);
            setUserType(LogicalUserType.RED_PANDA_USER);
		}

		this.setFirstName(user.getFirstName());
		this.setLastName(user.getLastName());
		this.setEmailAddress(user.getEmailAddress());
		this.setIsActivated(user.getIsActivated());
		this.setIsLoginEnabled(user.getIsLoginEnabled());

		roles = new HashSet<>();

		for (UserRole userRole : user.getUserRoles())
		{
			roles.add(userRole.getUserRoleType());
		}
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

	public Boolean getIsActivated()
	{
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated)
	{
		this.isActivated = isActivated;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String publisherName)
	{
		this.accountName = publisherName;
	}

	public AccountType getAccountType()
	{
		return accountType;
	}

	public void setAccountType(AccountType accountType)
	{
		this.accountType = accountType;
	}

	public Set<UserRoleType> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<UserRoleType> roles)
	{
		this.roles = roles;
	}

	public void addRole(UserRoleType userRoleType)
	{
		roles.add(userRoleType);
	}

	public void removeRole(UserRoleType userRoleType)
	{
		roles.remove(userRoleType);
	}

	public boolean hasRole(UserRoleType userRoleType)
	{
		return roles.contains(userRoleType);
	}


	public Boolean getIsLoginEnabled()
	{
		return isLoginEnabled;
	}

	public void setIsLoginEnabled(Boolean isLoginEnabled)
	{
		this.isLoginEnabled = isLoginEnabled;
	}

	public String getActivationKey() throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userId);
		stringBuilder.append(emailAddress);

		return hashText(ACTIVATION_SALT, stringBuilder.toString());
	}

	public String getResetPasswordKey() throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userId);
		stringBuilder.append(emailAddress);

		return hashText(RESET_SALT, stringBuilder.toString());
	}

	public String getCreatePasswordKey() throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userId);
		stringBuilder.append(emailAddress);

		return hashText(CREATE_PASSWORD_SALT, stringBuilder.toString());
	}

	public String getRememberMeKey() throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userId);
		stringBuilder.append(emailAddress);

		return hashText(REMEMBER_ME_SALT, stringBuilder.toString());
	}

	private String hashText(String salt, String text) throws Exception
	{
		try
		{

			StringBuilder saltedPasswordBuilder = new StringBuilder();
			saltedPasswordBuilder.append(salt);
			saltedPasswordBuilder.append(text);

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(saltedPasswordBuilder.toString().getBytes());

			byte[] byteData = messageDigest.digest();

			StringBuffer stringBuffer = new StringBuffer();

			for (int i = 0; i < byteData.length; i++)
			{
				stringBuffer.append(Integer.toString((byteData[i] & 0xff + 0x100), 16).substring(1));
			}

			return stringBuffer.toString();

		}
		catch (NoSuchAlgorithmException exception)
		{
			log.error(exception);
			throw new Exception("Error while hashing password");
		}

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
