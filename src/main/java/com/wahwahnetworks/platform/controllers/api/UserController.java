package com.wahwahnetworks.platform.controllers.api;

import com.newrelic.api.agent.NewRelic;
import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.annotations.OAuthAllowScope;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.models.CreateUserModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UpdateUserAccountModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Justin on 5/17/2014.
 */

@RestController
@RequestMapping("/api/1.0/users/")
@Scope("request")
public class UserController extends BaseAPIController
{
	private static final Logger log = Logger.getLogger(UserController.class);

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/current/", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.USER)
	@OAuthAllowScope({"read","offline","platform"})
	public UserWebModel getCurrentUser(@RequestParam(value = "use-real-user", defaultValue = "false") Boolean useRealUser)
	{

		UserModel userModel;

		if (useRealUser)
		{
			userModel = sessionModel.getRealUser();
		}
		else
		{
			userModel = sessionModel.getUser();
		}

		return new UserWebModel(userModel);
	}

	@RequestMapping(value = "/ping", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.USER)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void ping(){

	}

	@RequestMapping(value = "/current/", method = RequestMethod.PUT)
	@HasUserRole(UserRoleType.USER)
	public UserWebModel updateCurrentUser(@RequestBody UpdateProfileModel userWebModel)
	{

		UserModel userModel = sessionModel.getUser();
		userService.updateUser(userModel, userWebModel);

		return new UserWebModel(userModel);
	}

	@RequestMapping(value = "/current/update-password", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.USER)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public UserWebModel updatePassword(@RequestBody UpdatePasswordModel updatePasswordModel) throws Exception
	{

		UserModel userModel = sessionModel.getUser();
		userService.updatePassword(userModel, updatePasswordModel);

		return new UserWebModel(userModel);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.USER)
	public UserWebListModel listUsers(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize, @RequestParam(value = "use-real-user", defaultValue = "false") Boolean useRealUser)
	{

		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort("firstName","lastName"));

		UserModel user = sessionModel.getUser();

		if (useRealUser)
		{
			user = sessionModel.getRealUser();
		}

		return userService.listUsers(user, pageable);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.USER)
	public UserWebModel getUser(@PathVariable("userId") int userId)
	{

		UserModel userModel = userService.getUserById(userId);

		if (userModel == null)
		{
			throw new EntityNotFoundException("User with ID " + userId + " does not exist");
		}

		if(sessionModel.getUser().getAccountType() == AccountType.ROOT){
			return new UserWebModel(userModel);
		}

		if (sessionModel.getUser().getAccountType() == AccountType.NETWORK){
			if(userModel.getAccountId().equals(sessionModel.getUser().getAccountId())){
				return new UserWebModel(userModel);
			}
		}

        if (sessionModel.getUser().getAccountType() == AccountType.PUBLISHER){
            if(userModel.getAccountId().equals(sessionModel.getUser().getAccountId())){
                return new UserWebModel(userModel);
            }
        }

		if (sessionModel.getUser().hasRole(UserRoleType.USER))
		{
			if (userModel.getUserId() == sessionModel.getUser().getUserId())
			{
				return new UserWebModel(userModel);
			}
		}

		throw new EntityNotPermittedException("Access to user with ID " + userId + " not permitted");
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.USER)
	public void deleteUser(@PathVariable("userId") int userId)
	{
		UserModel userModel = userService.getUserById(userId);

		if (userModel == null)
		{
			throw new EntityNotFoundException("User with ID " + userId + " does not exist");
		}

		if (sessionModel.getUser().hasRole(UserRoleType.SUPER_USER))
		{
			userService.deleteUser(userModel);
			return;
		}

		if (sessionModel.getUser().hasRole(UserRoleType.PUBLISHER_ADMIN))
		{
			if (userModel.getAccountId() == sessionModel.getUser().getAccountId())
			{
				userService.deleteUser(userModel);
				return;
			}
		}

		if (sessionModel.getUser().hasRole(UserRoleType.USER))
		{
			if (userModel.getUserId() == sessionModel.getUser().getUserId())
			{
				userService.deleteUser(userModel);
				return;
			}
		}

		throw new EntityNotPermittedException("Access to user with ID " + userId + " not permitted");
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public UserWebModel createUser(@RequestBody CreateUserModel createUserModel, HttpServletRequest httpServletRequest) throws Exception
	{

		if (!sessionModel.getUser().hasRole(UserRoleType.SUPER_USER))
		{
			createUserModel.setAccountName(sessionModel.getUser().getAccountName());
		}

		UserModel createdUser = userService.createUser(createUserModel);

		if (createdUser != null)
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

			String emailAddressEncoded = Base64.encodeBase64String(createdUser.getEmailAddress().getBytes());

			activationServerUrl.append("/useraccount/create-password/" + emailAddressEncoded + "/" + createdUser.getCreatePasswordKey());

			userService.sendCreatePasswordEmail(createdUser, activationServerUrl.toString());

			return new UserWebModel(createdUser);
		}

		return null;

	}

	@RequestMapping(value = "/{userId}/roles", method = RequestMethod.PUT, consumes = "application/json")
	@HasUserRole(UserRoleType.USER)
	public UserWebModel updateRoles(@PathVariable("userId") int userId, @RequestBody List<String> roles)
	{
		UserModel userModel = userService.getUserById(userId);

		if (userModel == null)
		{
			throw new EntityNotFoundException("User with ID " + userId + " does not exist");
		}

		if (!sessionModel.getUser().hasRole(UserRoleType.DEVELOPER))
		{
			if ( !userModel.hasRole(UserRoleType.DEVELOPER))
			{
				while (roles.contains("DEVELOPER"))
				{
					roles.remove("DEVELOPER"); // Only developer can add developer (chicken-and-egg problem solved by direct database set for first developer)
				}
			}
			else
				if (!roles.contains("DEVELOPER"))
					roles.add("DEVELOPER");
		}

		if (sessionModel.getUser().hasRole(UserRoleType.SUPER_USER))
		{
			userModel = userService.updateUserRoles(userModel, roles);
			return new UserWebModel(userModel);
		}

		if (sessionModel.getUser().hasRole(UserRoleType.PUBLISHER_ADMIN))
		{

			// We need to ensure the roles are valid

			if (roles.contains("SUPER_USER"))
			{
				throw new EntityNotPermittedException("You do not have permission to set access role 'SUPER_USER'");
			}

			if (roles.contains("TOOLBAR_PUBLISHER"))
			{
				throw new EntityNotPermittedException("You do not have permission to set access role 'TOOLBAR_PUBLISHER'");
			}

			if (roles.contains("DEVELOPER"))
			{
				throw new EntityNotPermittedException("You do not have permission to set access role 'DEVELOPER'");
			}

			if (roles.contains("ADVERTISING_ADMIN"))
			{
				throw new EntityNotPermittedException("You do not have permission to set access role 'ADVERTISING_ADMIN'");
			}

			if (roles.contains("INTERNAL_USER"))
			{
				throw new EntityNotPermittedException("You do not have permission to set access role 'INTERNAL_USER'");
			}

			if (userModel.getAccountId() == sessionModel.getUser().getAccountId())
			{
				userModel = userService.updateUserRoles(userModel, roles);
				return new UserWebModel(userModel);
			}
			else
			{
				throw new EntityNotPermittedException("Access to user with ID " + userId + " not permitted");
			}
		}

		throw new EntityNotPermittedException("You do not have permission to set access roles");
	}

	@RequestMapping(value = "/{userId}/profile", method = RequestMethod.PUT, consumes = "application/json")
	@HasUserRole(UserRoleType.USER)
	public UserWebModel updateAccountProfile(@PathVariable("userId") int userId, @RequestBody UpdateUserAccountModel accountModel)
	{
		UserModel userModel = userService.getUserById(userId);

		if (userModel == null)
		{
			throw new EntityNotFoundException("User with ID " + userId + " does not exist");
		}

		if (sessionModel.getUser().hasRole(UserRoleType.SUPER_USER))
		{

			userService.updateUser(sessionModel.getUser(), userModel, accountModel);
			return new UserWebModel(userModel);
		}

		if (sessionModel.getUser().hasRole(UserRoleType.PUBLISHER_ADMIN))
		{
			if (userModel.getAccountId() == sessionModel.getUser().getAccountId())
			{
				userService.updateUser(sessionModel.getUser(), userModel, accountModel);
				return new UserWebModel(userModel);
			}
		}

		if (sessionModel.getUser().hasRole(UserRoleType.USER))
		{
			if (userModel.getUserId() == sessionModel.getUser().getUserId())
			{
				userService.updateUser(sessionModel.getUser(), userModel, accountModel);
				return new UserWebModel(userModel);
			}
		}

		throw new EntityNotPermittedException("Access to user with ID " + userId + " not permitted");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionInfoModel> handleExceptions(HttpServletRequest request, Exception exception)
	{
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		if (exception instanceof ModelValidationException)
		{
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		else if (exception instanceof EntityNotFoundException)
		{
			httpStatus = HttpStatus.NOT_FOUND;
		}
		else if (exception instanceof ServiceException)
		{
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		else if (exception instanceof EntityNotPermittedException)
		{
			httpStatus = HttpStatus.FORBIDDEN;
		}

		ExceptionInfoModel exceptionInfo = new ExceptionInfoModel();
		exceptionInfo.setUrl(request.getRequestURL().toString());
		exceptionInfo.setStatus(httpStatus.value());
		exceptionInfo.setMessage(exception.getMessage());

		NewRelic.noticeError(exception);
		log.error(exceptionInfo,exception);

		return new ResponseEntity<>(exceptionInfo, httpStatus);
	}

}
