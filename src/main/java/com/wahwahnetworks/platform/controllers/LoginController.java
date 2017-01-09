package com.wahwahnetworks.platform.controllers;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.models.*;
import com.wahwahnetworks.platform.services.AccountService;
import com.wahwahnetworks.platform.services.AuditService;
import com.wahwahnetworks.platform.services.PlatformService;
import com.wahwahnetworks.platform.services.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/useraccount/")
@Scope("request")
public class LoginController
{
	private static final Logger log = Logger.getLogger(LoginController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private AuditService auditService;

	@Autowired
	private PlatformService platformService;

	@Autowired
	private Environment environment;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage(ModelMap modelMap)
	{
		modelMap.put("commitId", platformService.getCommitId());

		log.info("showLoginPage");
		return "useraccount/login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginUser(@ModelAttribute("loginModel") LoginModel loginModel, HttpSession httpSession, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes) throws Exception
	{

		log.info("loginUser");

		boolean isValidCredentials = userService.validateCredentials(loginModel);

		if (isValidCredentials)
		{
			httpSession.setAttribute("logged_in", true);

			UserModel userModel = userService.getUserByEmailAddress(loginModel.getEmailAddress());
			sessionModel.setUser(userModel);
			sessionModel.setRealUser(userModel);

			if (loginModel.getRememberMe())
			{
				Cookie cookie = new Cookie("WAHWAH_REMEMBERME_TOKEN", userService.getRememberMeCookieValue(userModel));
				cookie.setMaxAge(604800); // 1 Week In Seconds
				cookie.setPath(httpServletRequest.getContextPath());
				cookie.setHttpOnly(true);

				httpServletResponse.addCookie(cookie);
			}
		}
		else
		{

			UserModel userModel = userService.getUserByEmailAddress(loginModel.getEmailAddress());

			if (userModel != null)
			{
				if (userModel.getIsActivated() == false)
				{

					String emailAddressEncoded = Base64.encodeBase64String(userModel.getEmailAddress().getBytes());

					redirectAttributes.addFlashAttribute("needsActivation", true);
					redirectAttributes.addFlashAttribute("emailAddressEncoded", emailAddressEncoded);
					return "redirect:/useraccount/login";
				}
			}

			redirectAttributes.addFlashAttribute("errorMessage", "<p class=\"error_message\">We could not log you in. This could be because your username or password is incorrect, or your user account has not yet been activated. If you have not activated your user account, please do so and try again.</p>");
			return "redirect:/useraccount/login";
		}

		String redirectUri = "/";

		if (httpSession.getAttribute("redirect_uri") != null)
		{
			redirectUri = (String) httpSession.getAttribute("redirect_uri");
			httpSession.setAttribute("redirect_uri", "/");
		}

		return "redirect:" + redirectUri;
	}

	@RequestMapping(value = "/impersonate", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.USER)
	// Intentionally not using SUPER_USER because HasUserRole uses current user, not realUser. Check in function instead
	public String impersonateUser(@ModelAttribute("impersonateModel") ImpersonateModel impersonateModel, HttpSession httpSession) throws Exception
	{
		log.info("impersonateUser");

		UserModel realUser = sessionModel.getRealUser();    // Use real user, not user since could be impersonating already

		if (!realUser.hasRole(UserRoleType.SUPER_USER))
			throw new EntityNotPermittedException("Access not permitted for limited user");

		sessionModel.clearSession();

		UserModel userToImpersonate = userService.getUserById(impersonateModel.getImpersonateUserId());

		httpSession.setAttribute("logged_in", true);
		sessionModel.setUser(userToImpersonate);
		sessionModel.setRealUser(realUser);

		if (realUser.getUserId() != userToImpersonate.getUserId())
		{
			auditService.addAuditEntry(realUser, AuditActionTypeEnum.USER_IMPERSONATE, "User impersonated: [user:" + userToImpersonate.getEmailAddress() + "]", "User Name: " + userToImpersonate.getFirstName() + " " + userToImpersonate.getLastName());
		}

		return "redirect:/";
	}


	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutUser(HttpSession httpSession, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		httpSession.setAttribute("logged_in", false);
//        httpSession.invalidate();

		Cookie cookie = new Cookie("WAHWAH_REMEMBERME_TOKEN", "");
		cookie.setMaxAge(0); // Expire Instantly
		cookie.setPath(httpServletRequest.getContextPath());
		cookie.setHttpOnly(true);

		httpServletResponse.addCookie(cookie);

		return "redirect:/";
	}

	@RequestMapping(value = "/forgot_password", method = RequestMethod.GET)
	public String showForgotPassword()
	{
		log.info("showForgotPassword");
		return "useraccount/forgot_password";
	}

	@RequestMapping(value = "/forgot_password", method = RequestMethod.POST)
	public String sendResetPasswordEmail(@ModelAttribute("emailAddress") String emailAddress, HttpServletRequest httpServletRequest) throws Exception
	{

		UserModel userModel = userService.getUserByEmailAddress(emailAddress);

		if (userModel != null)
		{
			String serverName = httpServletRequest.getServerName();
			int serverPort = httpServletRequest.getServerPort();
			String serverContext = httpServletRequest.getContextPath();

			StringBuilder resetPasswordUrl = new StringBuilder();
			resetPasswordUrl.append("http://");
			resetPasswordUrl.append(serverName);

			if (serverPort != 80)
			{
				resetPasswordUrl.append(":");
				resetPasswordUrl.append(serverPort);
			}

			resetPasswordUrl.append(serverContext);
			String emailAddressEncoded = Base64.encodeBase64String(userModel.getEmailAddress().getBytes());
			resetPasswordUrl.append("/useraccount/reset_password/" + emailAddressEncoded + "/" + userModel.getResetPasswordKey());

			userService.sendResetEmail(userModel, resetPasswordUrl.toString());
		}

		return "useraccount/forgot_password_email_sent";
	}

	@RequestMapping(value = "/reset_password/{emailAddressEncoded}/{resetPasswordKey}", method = RequestMethod.GET)
	public String showResetPasswordFromEmail(@PathVariable String emailAddressEncoded, @PathVariable String resetPasswordKey, ModelMap modelMap) throws Exception
	{
		log.info("showResetPasswordFromEmail");

		String emailAddress = new String(Base64.decodeBase64(emailAddressEncoded));
		boolean canResetPassword = userService.validateResetPasswordKey(emailAddress, resetPasswordKey);

		modelMap.put("canResetPassword", canResetPassword);
		modelMap.put("passwordsDoNotMatch", false);

		return "useraccount/reset_password";
	}

	@RequestMapping(value = "/reset_password/{emailAddressEncoded}/{resetPasswordKey}", method = RequestMethod.POST)
	public String resetPasswordFromEmail(@PathVariable String emailAddressEncoded, @PathVariable String resetPasswordKey, @ModelAttribute("changePasswordModel") ChangePasswordModel changePasswordModel, ModelMap modelMap) throws Exception
	{
		log.info("resetPasswordFromEmail");

		String emailAddress = new String(Base64.decodeBase64(emailAddressEncoded));
		boolean canResetPassword = userService.validateResetPasswordKey(emailAddress, resetPasswordKey);

		if (canResetPassword)
		{
			UserModel userModel = userService.getUserByEmailAddress(emailAddress);
			boolean passwordChangedSuccessfully = userService.changePassword(userModel, changePasswordModel);

			if (!passwordChangedSuccessfully)
			{

				modelMap.put("canResetPassword", canResetPassword);
				modelMap.put("passwordsDoNotMatch", true);

				return "useraccount/reset_password";
			}
		}

		modelMap.put("canResetPassword", canResetPassword);

		return "useraccount/reset_password_done";
	}

	@RequestMapping(value = "/create-password/{emailAddressEncoded}/{createPasswordKey}", method = RequestMethod.GET)
	public String showCreatePasswordFromEmail(@PathVariable String emailAddressEncoded, @PathVariable String createPasswordKey, ModelMap modelMap) throws Exception
	{
		log.info("showCreatePasswordFromEmail");

		String emailAddress = new String(Base64.decodeBase64(emailAddressEncoded));
		boolean canCreatePassword = userService.validateCreatePasswordKey(emailAddress, createPasswordKey);

		modelMap.put("canCreatePassword", canCreatePassword);
		modelMap.put("passwordsDoNotMatch", false);

		return "useraccount/create_password";
	}

	@RequestMapping(value = "/create-password/{emailAddressEncoded}/{createPasswordKey}", method = RequestMethod.POST)
	public String createPasswordFromEmail(@PathVariable String emailAddressEncoded, @PathVariable String createPasswordKey, @ModelAttribute("changePasswordModel") ChangePasswordModel changePasswordModel, ModelMap modelMap) throws Exception
	{
		log.info("createPasswordFromEmail");

		String emailAddress = new String(Base64.decodeBase64(emailAddressEncoded));
		boolean canCreatePassword = userService.validateCreatePasswordKey(emailAddress, createPasswordKey);

		if (canCreatePassword)
		{
			UserModel userModel = userService.getUserByEmailAddress(emailAddress);
			boolean passwordChangedSuccessfully = userService.changePassword(userModel, changePasswordModel);

			if (!passwordChangedSuccessfully)
			{

				modelMap.put("canCreatePassword", canCreatePassword);
				modelMap.put("passwordsDoNotMatch", true);

				return "useraccount/create_password";
			}

			userService.activateUser(emailAddress, userModel.getActivationKey());
		}

		modelMap.put("canCreatePassword", canCreatePassword);

		return "useraccount/create_password_done";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegisterPage()
	{
		log.info("showRegisterPage");
		return "useraccount/register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@Transactional
	public String registerUser(@ModelAttribute("registrationModel") RegistrationModel registrationModel, HttpServletRequest httpServletRequest, ModelMap modelMap) throws Exception
	{
		log.info("registerUser");

		// Validate Account Name
		String accountName = registrationModel.getSiteName();
		Account existingAccount = accountService.findByName(accountName);

        if(existingAccount != null){
            modelMap.put("hasErrorMessage",true);
            modelMap.put("errorMessage","The site name supplied is already taken. Please enter a different name. If you are a member of the existing site, please contact your administrator to be added to the existing site");
            return "useraccount/register";
        }

		// Validate Email Address
		String emailAddress = registrationModel.getEmailAddress();
		UserModel existingUser = userService.getUserByEmailAddress(emailAddress);

		if(existingUser != null){
		    modelMap.put("hasErrorMessage",true);
		    modelMap.put("errorMessage","A user with that email address has already been registered. Please login instead. If you forgot your password, you can reset it instead.");
		    return "useraccount/register";
		}

		// Validate Accept Terms
		if(!registrationModel.getAcceptTerms()){
			modelMap.put("hasErrorMessage",true);
			modelMap.put("errorMessage","You need to accept the Terms of Service in order to register.");
			return "useraccount/register";
		}

        UserModel userModel = userService.createUser(registrationModel);

		if(userModel != null) {
            // Send Confirmation Email
            String firstName = registrationModel.getFirstName();
            String lastName = registrationModel.getLastName();
            String siteName = registrationModel.getSiteName();
            String siteURL = registrationModel.getSiteURL();

            if ((firstName == null) || (lastName == null) || (emailAddress == null) || (siteName == null) || (siteURL == null)) {
                modelMap.put("hasErrorMessage", true);
                modelMap.put("errorMessage", "All fields are required.");
                return "useraccount/register";
            }

            String serverName = httpServletRequest.getServerName();
            int serverPort = httpServletRequest.getServerPort();
            String serverContext = httpServletRequest.getContextPath();

            StringBuilder activationServerUrl = new StringBuilder();
            activationServerUrl.append("http://");
            activationServerUrl.append(serverName);

            if (serverPort != 80) {
                activationServerUrl.append(":");
                activationServerUrl.append(serverPort);
            }

            activationServerUrl.append(serverContext);

            String emailAddressEncoded = Base64.encodeBase64String(emailAddress.getBytes());
			activationServerUrl.append("/useraccount/create-password/" + emailAddressEncoded + "/" + userModel.getCreatePasswordKey());

			if(environment == Environment.PRODUCTION){
				userService.sendRegistrationEmail(registrationModel);
			}

            userService.sendCreatePasswordEmail(userModel,activationServerUrl.toString());
        }

		modelMap.put("firstName",userModel.getFirstName());

		return "useraccount/confirm_registration";
	}

	@RequestMapping(value = "/activate/resend/{encodedEmailAddress}", method = RequestMethod.GET)
	public String resendActivationEmail(@PathVariable String encodedEmailAddress, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) throws Exception
	{
		String emailAddress = new String(Base64.decodeBase64(encodedEmailAddress));

		UserModel userModel = userService.getUserByEmailAddress(emailAddress);

		if (userModel != null)
		{
			String serverName = httpServletRequest.getServerName();
			int serverPort = httpServletRequest.getServerPort();
			String serverContext = httpServletRequest.getContextPath();

			StringBuilder activationServerUrl = new StringBuilder();
			activationServerUrl.append("http://");
			activationServerUrl.append(serverName);

			if (serverPort != 80)
			{
				activationServerUrl.append(":");
				activationServerUrl.append(serverPort);
			}

			activationServerUrl.append(serverContext);

			String emailAddressEncoded = Base64.encodeBase64String(userModel.getEmailAddress().getBytes());

			activationServerUrl.append("/useraccount/activate/" + emailAddressEncoded + "/" + userModel.getActivationKey());

			userService.sendActivationEmail(userModel, activationServerUrl.toString());

		}


		redirectAttributes.addFlashAttribute("errorMessage", "<p class=\"error_message\">We have resent your activation email to the email address provided.</p>");

		return "redirect:/useraccount/login";
	}

	@RequestMapping(value = "/activate/{encodedEmailAddress}/{activationKey}", method = RequestMethod.GET)
	public String activateUser(@PathVariable String encodedEmailAddress, @PathVariable String activationKey, ModelMap modelMap)
	{

		try
		{
			String emailAddress = new String(Base64.decodeBase64(encodedEmailAddress));

			boolean didActivate = userService.activateUser(emailAddress, activationKey);
			modelMap.put("didActivate", didActivate);
		}
		catch (Exception ex)
		{
			log.error(ex);
			modelMap.put("didActivate", false);
		}

		return "useraccount/activation";
	}
}