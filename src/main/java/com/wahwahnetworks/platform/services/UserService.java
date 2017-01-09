package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountFree;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.data.entities.UserRole;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.entities.enums.Country;
import com.wahwahnetworks.platform.data.entities.enums.Language;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.data.repos.AccountRepository;
import com.wahwahnetworks.platform.data.repos.UserRepository;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.lib.AESUtil;
import com.wahwahnetworks.platform.lib.HMacUtil;
import com.wahwahnetworks.platform.models.*;
import com.wahwahnetworks.platform.models.web.UpdatePasswordModel;
import com.wahwahnetworks.platform.models.web.UpdateProfileModel;
import com.wahwahnetworks.platform.models.web.UserWebListModel;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Justin on 5/12/2014.
 */

@Service
public class UserService
{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private AuditService auditService;

	private final String HMAC_KEY = "floccinaucinihilipilification "; // Dictionary.com WOTD on 2015-06-04

	@Transactional
	public UserModel createUser(RegistrationModel registrationModel) throws Exception
	{
		AccountFree account = new AccountFree();
		account.setName(registrationModel.getSiteName());
		account.setContactName(registrationModel.getFirstName() + " " + registrationModel.getLastName());
		account.setContactEmail(registrationModel.getEmailAddress());
		account.setCountry(Country.UNITED_STATES);
		account.setLanguage(Language.en);
		accountRepository.save(account);

		User user = new User();
		user.setFirstName(registrationModel.getFirstName());
		user.setLastName(registrationModel.getLastName());
		user.setEmailAddress(registrationModel.getEmailAddress());
		user.setIsActivated(false);
		user.setIsLoginEnabled(true);
		user.addUserRoleType(UserRoleType.USER);
		user.addUserRoleType(UserRoleType.ANALYTICS);
        user.addUserRoleType(UserRoleType.NETWORK_ADMIN);
		user.setPassword("****");

		user.setAccount(account);

		userRepository.save(user);

		String auditDescription = String.format("[user:%s] signed up for service with account name: %s", registrationModel.getEmailAddress(), registrationModel.getSiteName());
		auditService.addAuditEntry(user, AuditActionTypeEnum.USER_REGISTER, auditDescription, null);

		return new UserModel(user);
	}

	@Transactional
	public UserModel createUser(CreateUserModel userWebModel) throws Exception
	{

		if (userRepository.findByEmailAddress(userWebModel.getEmailAddress()) != null)
		{
			throw new Exception("Duplicate email"); // common user error
		}
		User user = new User();
		user.setFirstName(userWebModel.getFirstName());
		user.setLastName(userWebModel.getLastName());
		user.setEmailAddress(userWebModel.getEmailAddress());
		user.setPassword("****");
		user.setIsActivated(false);
		user.setIsLoginEnabled(true);

		user.addUserRoleType(UserRoleType.USER);

		for (String roleName : userWebModel.getRoles())
		{
			UserRoleType roleType = UserRoleType.valueOf(roleName);
			user.addUserRoleType(roleType);
		}

		String publisherName = userWebModel.getAccountName();

		if (publisherName == null && !user.hasUserRole(UserRoleType.INTERNAL_USER))
		{
			throw new ServiceException("Publisher required for non-internal users");
		}

		if (publisherName != null && !user.hasUserRole(UserRoleType.INTERNAL_USER))
		{
			Account account = accountRepository.findByName(publisherName);
			user.setAccount(account);
		}

		String auditDescription = String.format("[user:%s] was created", userWebModel.getEmailAddress());
		auditService.addAuditEntry(AuditActionTypeEnum.USER_CREATED, auditDescription, null);

		userRepository.save(user);

		return new UserModel(user);
	}

	@Transactional
	private void updateUser(UserModel userModel)
	{
		User user = userRepository.findOne(userModel.getUserId());
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setEmailAddress(userModel.getEmailAddress());
		user.setIsActivated(userModel.getIsActivated());
		user.setIsLoginEnabled(userModel.getIsLoginEnabled());

		Integer publisherId = userModel.getAccountId();

		if (publisherId != null)
		{
			Account account = null;
			if (userModel.getAccountId() != null)
			{
				account = accountRepository.findOne(userModel.getAccountId());
			}
			user.setAccount(account);
		}
		else
		{
			user.setAccount(null);
		}

		Set<UserRole> currentRoles = new HashSet<>(user.getUserRoles());
		Set<UserRoleType> currentRoleTypes = new HashSet<>();

		for (UserRole userRole : currentRoles)
		{
			currentRoleTypes.add(userRole.getUserRoleType());
		}

		// Remove Old Roles
		for (UserRoleType roleType : currentRoleTypes)
		{
			if (!userModel.getRoles().contains(roleType))
			{
				user.removeUserRoleType(roleType);
			}
		}

		// Add New Roles
		for (UserRoleType roleType : userModel.getRoles())
		{
			if (!currentRoleTypes.contains(roleType))
			{
				user.addUserRoleType(roleType);
			}
		}

		String auditDescription = String.format("[user:%s] was updated", userModel.getEmailAddress());
		auditService.addAuditEntry(user, AuditActionTypeEnum.USER_UPDATE, auditDescription, null);

		userRepository.save(user);
	}

	@Transactional
	public void updateUser(UserModel currentUser, UpdateProfileModel newUserFromWeb)
	{

		String updatedFields = "";

		if (!currentUser.getFirstName().equals(newUserFromWeb.getFirstName())) updatedFields += ",First Name";
		if (!currentUser.getLastName().equals(newUserFromWeb.getLastName())) updatedFields += ",Last Name";
		if (!currentUser.getEmailAddress().equals(newUserFromWeb.getEmailAddress())) updatedFields += ",Email Address";

		currentUser.setFirstName(newUserFromWeb.getFirstName());
		currentUser.setLastName(newUserFromWeb.getLastName());
		currentUser.setEmailAddress(newUserFromWeb.getEmailAddress());

		if (updatedFields.startsWith(","))
		{
			updatedFields = updatedFields.substring(1);
		}

		if (!updatedFields.isEmpty())
		{
			String auditDescription = String.format("Profile fields updated: " + updatedFields);
			auditService.addAuditEntry(AuditActionTypeEnum.USER_PROFILEUPDATE, auditDescription, null);
		}

		updateUser(currentUser);
	}

	@Transactional
	public UserModel updateUserRoles(UserModel user, List<String> roles)
	{

		for (String roleName : roles)
		{
			UserRoleType roleType = UserRoleType.valueOf(roleName);

			if (!user.hasRole(roleType))
			{
				user.addRole(roleType);
			}
		}

		List<UserRoleType> oldRoles = new ArrayList<>(user.getRoles());

		for (UserRoleType roleType : oldRoles)
		{
			String roleName = roleType.toString();

			if (!roles.contains(roleName))
			{
				user.removeRole(roleType);
			}
		}

		if (user.hasRole(UserRoleType.INTERNAL_USER))
		{
			// INTERNAL_USER users do not have a publisher association
			user.setAccountId(0);
			user.setAccountName(null);
		}

		updateUser(user);

		return user;
	}

	@Transactional
	public UserModel updateUser(UserModel currentUser, UserModel user, UpdateUserAccountModel updateUserAccountModel)
	{


		if (updateUserAccountModel.isLoginEnabled() != null)
			user.setIsLoginEnabled(updateUserAccountModel.isLoginEnabled());

		user.setFirstName(updateUserAccountModel.getFirstName());
		user.setLastName(updateUserAccountModel.getLastName());
		user.setEmailAddress(updateUserAccountModel.getEmailAddress());

		if (currentUser.hasRole(UserRoleType.INTERNAL_USER))
		{

			String publisherName = updateUserAccountModel.getAccountName();

			if (publisherName == null || publisherName.isEmpty())
			{

				if (!user.hasRole(UserRoleType.INTERNAL_USER))
				{
					throw new ServiceException("Publisher required for users without INTERNAL_USER role");
				}

				user.setAccountId(0);
				user.setAccountName(null);

			}
			else
			{

				if (user.hasRole(UserRoleType.INTERNAL_USER))
				{
					user.removeRole(UserRoleType.INTERNAL_USER); // Saving Publisher removes INTERNAL_USER role automatically.
					user.removeRole(UserRoleType.SUPER_USER); // Saving Publisher removes SUPER_USER role automatically.
					user.removeRole(UserRoleType.DEVELOPER); // Saving Publisher removes DEVELOPER role automatically.
				}

				Account account = accountRepository.findByName(updateUserAccountModel.getAccountName());

				if (account == null)
				{
					throw new ServiceException("Publisher with name: " + updateUserAccountModel.getAccountName() + " does not exist");
				}

				user.setAccountId(account.getId());
				user.setAccountName(account.getName());
			}
		}

		updateUser(user);

		return user;
	}

	@Transactional
	public void deleteUser(UserModel userModel)
	{
		userRepository.delete(userModel.getUserId());
	}

	@Transactional
	public UserModel getUserById(int userId)
	{
		User user = userRepository.findOne(userId);

		if (user != null)
		{
			UserModel userModel = new UserModel(user);
			return userModel;
		}

		return null;
	}

	@Transactional
	public UserModel getUserByEmailAddress(String emailAddress)
	{
		User user = userRepository.findByEmailAddress(emailAddress);

		if (user != null)
		{
			return new UserModel(user);
		}

		return null;
	}

	@Transactional
	public Boolean validateCredentials(LoginModel loginModel) throws Exception
	{
		User user = userRepository.findByEmailAddress(loginModel.getEmailAddress());

		if (user != null && user.getIsActivated() && user.getIsLoginEnabled() && user.comparePassword(loginModel.getPassword()))
		{
			return true;
		}

		return false;
	}

	public void sendActivationEmail(UserModel userModel, String activationMailUrl)
	{

		StringBuilder activationMessageBuilder = new StringBuilder();
		activationMessageBuilder.append("Dear ");
		activationMessageBuilder.append(userModel.getFirstName());
		activationMessageBuilder.append(",");
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("Welcome to the Red Panda Platform!\r\n");
		activationMessageBuilder.append("In order to get started, please confirm your email address by visiting the link below:\r\n");
		activationMessageBuilder.append(activationMailUrl);
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("Thank You!\r\n");
		activationMessageBuilder.append("The Red Panda Platform Team");

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("hello@redpandaplatform.com");
		activationMessage.setTo(userModel.getEmailAddress());
		activationMessage.setSubject("Red Panda Platform account activation");
		activationMessage.setText(activationMessageBuilder.toString()); // TODO: Convert to HTML Message Some Day

		mailSender.send(activationMessage);
	}

	public void sendRegistrationEmail(RegistrationModel registrationModel)
	{
		StringBuilder activationMessageBuilder = new StringBuilder();

		activationMessageBuilder.append("Dear ");
		activationMessageBuilder.append("Red Panda Platform");
		activationMessageBuilder.append(",");
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("We have a new registration request.\r\n");
		activationMessageBuilder.append("\r\n");
		activationMessageBuilder.append("First Name :: " + registrationModel.getFirstName() + "\r\n");
		activationMessageBuilder.append("Last Name :: " + registrationModel.getLastName() + "\r\n");
		activationMessageBuilder.append("Email Address :: " + registrationModel.getEmailAddress() + "\r\n");
		activationMessageBuilder.append("Site Name :: " + registrationModel.getSiteName() + "\r\n");
		activationMessageBuilder.append("Site URL :: " + registrationModel.getSiteURL() + "\r\n");

		if(registrationModel.getTrafficEstimate() != null) {
			activationMessageBuilder.append("Traffic Estimate :: " + registrationModel.getTrafficEstimate() + "\r\n");
		} else {
			activationMessageBuilder.append("Traffic Estimate :: Not Supplied");
		}

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("hello@redpandaplatform.com");
		activationMessage.setTo("hello@redpandaplatform.com");
		activationMessage.setSubject("Red Panda Platform Registration Request");
		activationMessage.setText(activationMessageBuilder.toString()); // TODO: Convert to HTML Message Some Day

		mailSender.send(activationMessage);
	}

	public void sendCreatePasswordEmail(UserModel userModel, String createPasswordUrl)
	{
		StringBuilder activationMessageBuilder = new StringBuilder();
        activationMessageBuilder.append("Welcome ").append(userModel.getFirstName()).append(" to Red Panda Platform!\r\n\r\n");
        activationMessageBuilder.append("An account for you has been created. To complete setup, please click the link below to choose a password and start setting up your demand sources.\r\n");
        activationMessageBuilder.append(createPasswordUrl);
        activationMessageBuilder.append("\r\n\r\n");
        activationMessageBuilder.append("Thank You!\r\n");
        activationMessageBuilder.append("The Red Panda Platform Team");

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("hello@redpandaplatform.com");
		activationMessage.setTo(userModel.getEmailAddress());
		activationMessage.setSubject("Red Panda Platform account created");
		activationMessage.setText(activationMessageBuilder.toString());

		mailSender.send(activationMessage);
	}

	@Transactional
	public boolean activateUser(String emailAddress, String activationKey) throws Exception
	{

		UserModel userModel = getUserByEmailAddress(emailAddress);

		if (userModel != null && !userModel.getIsActivated() && userModel.getActivationKey().equals(activationKey))
		{

			userModel.setIsActivated(true);
			updateUser(userModel);

			return true;
		}

		return false;
	}

	@Transactional
	public boolean validateResetPasswordKey(String emailAddress, String resetPasswordKey) throws Exception
	{

		UserModel userModel = getUserByEmailAddress(emailAddress);

		if (userModel != null && userModel.getResetPasswordKey().equals(resetPasswordKey))
		{
			return true;
		}

		return false;
	}

	@Transactional
	public boolean validateCreatePasswordKey(String emailAddress, String createPasswordKey) throws Exception
	{

		UserModel userModel = getUserByEmailAddress(emailAddress);

		if (userModel != null && userModel.getCreatePasswordKey().equals(createPasswordKey))
		{
			return true;
		}

		return false;
	}

	public void sendResetEmail(UserModel userModel, String resetPasswordUrl) throws Exception
	{

		StringBuilder activationMessageBuilder = new StringBuilder();
		activationMessageBuilder.append("Dear ");
		activationMessageBuilder.append(userModel.getFirstName());
		activationMessageBuilder.append(",");
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("We've received a request to reset your password on the Red Panda Platform. If you didn't make the request, just ignore this email. Otherwise, you can reset your password using this link:\r\n");
		activationMessageBuilder.append(resetPasswordUrl);
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("Thank You!\r\n");
		activationMessageBuilder.append("The Red Panda Platform Team");

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("hello@redpandaplatform.com");
		activationMessage.setTo(userModel.getEmailAddress());
		activationMessage.setSubject("Red Panda Platform account password reset");
		activationMessage.setText(activationMessageBuilder.toString()); // TODO: Convert to HTML Message Some Day

		mailSender.send(activationMessage);
	}

	public void sendPasswordChangedEmail(UserModel userModel) throws Exception
	{
		StringBuilder activationMessageBuilder = new StringBuilder();
		activationMessageBuilder.append(userModel.getFirstName());
		activationMessageBuilder.append(",");
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("The password for Red Panda Platform has been changed or created.  If you made this request, you can safely disregard this email. Otherwise, please secure your account, or ask your account representative for more details.");
		activationMessageBuilder.append("\r\n\r\n");
		activationMessageBuilder.append("Thank You!\r\n");
		activationMessageBuilder.append("The Red Panda Platform Team");

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("hello@redpandaplatform.com");
		activationMessage.setTo(userModel.getEmailAddress());
		activationMessage.setSubject("Red Panda Platform password change notification");
		activationMessage.setText(activationMessageBuilder.toString()); // TODO: Convert to HTML Message Some Day

		mailSender.send(activationMessage);
	}

	@Transactional
	public boolean changePassword(UserModel userModel, ChangePasswordModel passwordModel) throws Exception
	{
		if (passwordModel.doPasswordsMatch())
		{
			User user = userRepository.findOne(userModel.getUserId());
			user.setPassword(passwordModel.getPassword());
			userRepository.save(user);

			sendPasswordChangedEmail(userModel);

			return true;
		}

		return false;
	}

	@Transactional
	public boolean updatePassword(UserModel userModel, UpdatePasswordModel updatePasswordModel) throws Exception
	{

		if (updatePasswordModel.doPasswordsMatch())
		{
			User user = userRepository.findOne(userModel.getUserId());

			if (user.comparePassword(updatePasswordModel.getCurrentPassword()))
			{
				user.setPassword(updatePasswordModel.getNewPassword());
				userRepository.save(user);

				sendPasswordChangedEmail(userModel);

				return true;
			}
			else
			{
				throw new ModelValidationException("Supplied Current Password Is Incorrect");
			}

		}
		else
		{
			throw new ModelValidationException("New Password and Confirm New Password Do Not Match");
		}

	}

	public String getRememberMeCookieValue(UserModel userModel) throws Exception
	{

		Instant expirationTime = Instant.now().plusSeconds(604800);

		SecureRandom secureRandom = new SecureRandom();
		byte[] ivData = secureRandom.generateSeed(16);
		String ivString = Base64.encodeBase64String(ivData);

		String rememberPasswordSecret = HMacUtil.hmac(userModel.getRememberMeKey(), HMAC_KEY);
		String keyString = rememberPasswordSecret;

		String token = userModel.getUserId() + ":" + userModel.getRememberMeKey() + ":" + expirationTime.getEpochSecond();

		String base64EncodedToken = AESUtil.encrypt(keyString, ivData, token);

		String tokenMac = HMacUtil.hmac(base64EncodedToken, HMAC_KEY);

		String authorizationToken = base64EncodedToken + "." + tokenMac + "." + userModel.getUserId() + "." + ivString;

		return authorizationToken;
	}

	public UserModel getUserFromRememberMeCookieValue(String authorizationToken)
	{
		try
		{
			int separatorIndex = authorizationToken.lastIndexOf(".");

			String messageContentsWithMac = authorizationToken.substring(0, separatorIndex);
			String ivString = authorizationToken.substring(separatorIndex + 1);

			separatorIndex = messageContentsWithMac.lastIndexOf(".");
			String messageContents = messageContentsWithMac.substring(0, separatorIndex);
			int userId = Integer.parseInt(messageContentsWithMac.substring(separatorIndex + 1));

			separatorIndex = messageContents.lastIndexOf(".");
			String base64EncodedToken = messageContents.substring(0, separatorIndex);
			String tokenMac = messageContents.substring(separatorIndex + 1);

			// Verify Message
			if (HMacUtil.hmac(base64EncodedToken, HMAC_KEY).equals(tokenMac))
			{
				UserModel userModel = getUserById(userId);

				if (userModel != null)
				{
					String rememberPasswordSecret = HMacUtil.hmac(userModel.getRememberMeKey(), HMAC_KEY);
					String keyString = rememberPasswordSecret;

					byte[] ivData = Base64.decodeBase64(ivString);

					String token = AESUtil.decrypt(keyString, ivData, base64EncodedToken);

					int userIdInToken = Integer.parseInt(token.substring(0, token.indexOf(":")));
					String rememberMeKeyInToken = token.substring(token.indexOf(":") + 1, token.lastIndexOf(":"));
					Integer epochExpiresTime = Integer.parseInt(token.substring(token.lastIndexOf(":") + 1));
					Instant expiresTime = Instant.ofEpochSecond(epochExpiresTime);

					boolean isExpired = Instant.now().isAfter(expiresTime);

					// Verify token was issued for same grant requested -- and it's not expired
					if (userIdInToken == userId && rememberMeKeyInToken.equals(userModel.getRememberMeKey()) && !isExpired)
					{
						return userModel;
					}
				}
			}
		}
		catch (Exception ex)
		{
			return null;
		}

		return null;
	}

	@Transactional(readOnly = true)
	public UserWebListModel listUsers(UserModel currentUser, Pageable pageable)
	{

		UserWebListModel userList = null;

		if (currentUser.hasRole(UserRoleType.INTERNAL_USER))
		{
			Page<User> users = userRepository.findAll(pageable);
			userList = new UserWebListModel(users);
		}
		else if (currentUser.hasRole(UserRoleType.PUBLISHER_ADMIN))
		{
			Page<User> users = userRepository.findByAccountId(currentUser.getAccountId(), pageable);
			userList = new UserWebListModel(users);
		}
		else if (currentUser.hasRole(UserRoleType.USER))
		{
			List<UserModel> users = new ArrayList<>();
			userList = new UserWebListModel(users);
		}

		return userList;
	}
}
