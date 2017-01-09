package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Justin on 5/12/2014.
 */

@Entity
@Table(name = "users")
public class User
{

	private static final Logger log = Logger.getLogger(User.class);

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = true)
	private Account account;

	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "password")
	private String hashedPassword;

	@Column(name = "is_activated")
	private boolean isActivated;

	@Column(name = "login_enabled")
	private boolean isLoginEnabled;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	private Set<UserRole> userRoleSet;

	public User()
	{
		userRoleSet = new HashSet<>();
	}

	// Use to salt the password -- Merriam-Webster's word of the day on May 13, 2014. Change if database is compromised!
	private static final String PASSWORD_SALT = "yokel";

	public int getId()
	{
		return this.id;
	}

	public Account getAccount()
	{
		return this.account;
	}

	public void setAccount(Account account)
	{
		this.account = account;
	}

	public String getEmailAddress()
	{
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public Boolean getIsActivated()
	{
		return this.isActivated;
	}

	public Set<UserRole> getUserRoles()
	{
		return userRoleSet;
	}

	public void addUserRoleType(UserRoleType userRoleType)
	{
		if (hasUserRole(userRoleType)) return;

		UserRole userRole = new UserRole(this, userRoleType);
		userRoleSet.add(userRole);
	}

	public void removeUserRoleType(UserRoleType userRoleType)
	{
		UserRole userRoleToRemove = null;

		for (UserRole userRole : userRoleSet)
		{
			if (userRole.getUserRoleType().equals(userRoleType))
			{
				userRoleToRemove = userRole;
				break;
			}
		}

		if (userRoleToRemove != null)
		{
			userRoleSet.remove(userRoleToRemove);
		}
	}

	public boolean hasUserRole(UserRoleType userRoleType)
	{
		for (UserRole userRole : userRoleSet)
		{
			if (userRole.getUserRoleType().equals(userRoleType))
			{
				return true;
			}
		}

		return false;
	}

	public void setIsActivated(Boolean isActivated)
	{
		this.isActivated = isActivated;
	}

	public void setPassword(String password) throws Exception
	{
		String hashedPassword = hashPassword(password);
		this.hashedPassword = hashedPassword;
	}

	public boolean comparePassword(String passwordToCompare) throws Exception
	{

		try
		{
			if (hashedPassword == null)
			{
				return false;
			}

			String hashedPasswordToCompare = hashPassword(passwordToCompare);
			return hashedPasswordToCompare.equals(hashedPassword);
		}
		catch (Exception exception)
		{
			return false;
		}
	}

	private String hashPassword(String password) throws Exception
	{
		try
		{

			StringBuilder saltedPasswordBuilder = new StringBuilder();
			saltedPasswordBuilder.append(PASSWORD_SALT);
			saltedPasswordBuilder.append(password);

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


	public boolean getIsLoginEnabled()
	{
		return isLoginEnabled;
	}

	public void setIsLoginEnabled(boolean isLoginEnabled)
	{
		this.isLoginEnabled = isLoginEnabled;
	}

}
