package com.wahwahnetworks.platform.controllers;

import com.wahwahnetworks.platform.data.entities.OAuthApplication;
import com.wahwahnetworks.platform.data.entities.OAuthToken;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.models.LoginModel;
import com.wahwahnetworks.platform.models.OAuthGrantModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.OAuthTokenModel;
import com.wahwahnetworks.platform.services.OAuthService;
import com.wahwahnetworks.platform.services.PlatformService;
import com.wahwahnetworks.platform.services.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jhaygood on 3/2/15.
 */

@Controller
@RequestMapping("/oauth/")
@Scope("request")
public class OAuthController
{
	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private OAuthService oAuthService;

	@Autowired
	private UserService userService;

	@Autowired
	private PlatformService platformService;

	@RequestMapping(value = "authorize", method = RequestMethod.GET)
	public String authorize(HttpServletRequest httpRequest, ModelMap modelMap, @RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri, @RequestParam("response_type") String responseType, @RequestParam("scope") String scope) throws Exception
	{
		modelMap.put("commitId", platformService.getCommitId());

		String state = httpRequest.getParameter("state"); // For round-tripping purposes

		OAuthApplication application = oAuthService.getApplicationForClientId(clientId);

		Set<String> scopes = new HashSet<>(Arrays.asList(scope.split(",")));

		if (application != null)
		{

			if (application.getRequiresUserConsent())
			{

				modelMap.put("xsrfToken", sessionModel.getXsrfToken());
				modelMap.put("clientId", clientId);
				modelMap.put("redirectUri", redirectUri);
				modelMap.put("responseType", responseType);
				modelMap.put("scope", scope);
				modelMap.put("state", state);
				modelMap.put("application", application);
				modelMap.put("scopes", scopes);

				return "oauth/get_consent";
			}
			else
			{
				if (responseType.equals("code") && application.getRedirectUris().contains(redirectUri))
				{
					String authorizationCode = oAuthService.createAuthorizationCode(application, sessionModel.getUser(), scopes);
					String redirectUrl = redirectUri + "?code=" + URLEncoder.encode(authorizationCode, "UTF-8") + "&state=" + state;
					return "redirect:" + redirectUrl;
				}
			}

		}
		else
		{
			throw new EntityNotPermittedException("Unknown Client ID");
		}

		return "Hello";
	}

	@RequestMapping(value = "grant_access", method = RequestMethod.POST)
	public String doGrantAccess(OAuthGrantModel grantModel) throws Exception
	{
		String xsrfToken = sessionModel.getXsrfToken();

		if (!grantModel.getXsrfToken().equals(xsrfToken))
		{
			throw new EntityNotPermittedException("Invalid XSRF Token");
		}


		Boolean doGrant = grantModel.getDoGrant();
		OAuthApplication application = oAuthService.getApplicationForClientId(grantModel.getClientId());

		if (!application.getRedirectUris().contains(grantModel.getRedirectUri()))
		{
			doGrant = false;
		}

		if (!doGrant)
		{
			String redirectUrl = grantModel.getRedirectUri() + "#error=access_denied";
			return "redirect:" + redirectUrl;
		}

		Set<String> scopes = new HashSet<>(Arrays.asList(grantModel.getScope().split(",")));


		if (grantModel.getResponseType().equals("code"))
		{
			String authorizationCode = oAuthService.createAuthorizationCode(application, sessionModel.getUser(), scopes);
			String redirectUrl = grantModel.getRedirectUri() + "?code=" + URLEncoder.encode(authorizationCode, "UTF-8") + "&state=" + grantModel.getState();

			return "redirect:" + redirectUrl;
		}

		throw new EntityNotPermittedException("Invalid Grant Request");
	}

	@RequestMapping(value = "token", method = RequestMethod.POST, produces = "application/json")
	public
	@ResponseBody
	OAuthTokenModel getBearerToken(HttpServletRequest httpServletRequest, @RequestParam("client_id") String clientId, @RequestParam("client_secret") String clientSecret, @RequestParam("grant_type") String grantType) throws Exception
	{
		OAuthApplication application = oAuthService.getApplicationForClientId(clientId);

		if (grantType.equals("authorization_token"))
		{
			String code = httpServletRequest.getParameter("code");

			OAuthToken oAuthToken = oAuthService.getTokenFromAuthorizationCode(code);

			if (oAuthToken != null)
			{
				String bearerToken = oAuthService.getBearerTokenFromAuthorizationCode(code, clientId, clientSecret);
				String scope = StringUtils.join(oAuthToken.getOauthScopes(), ",");

				OAuthTokenModel tokenModel = new OAuthTokenModel();
				tokenModel.setAccessToken(bearerToken);
				tokenModel.setTokenType("bearer");
				tokenModel.setExpiresIn(60 * 60);
				tokenModel.setScope(scope);

				return tokenModel;

			}
			else
			{
				throw new EntityNotPermittedException("Invalid Authentication Code");
			}
		}

		if(grantType.equals("password"))
		{
			String username = httpServletRequest.getParameter("username");
			String password = httpServletRequest.getParameter("password");

			LoginModel loginModel = new LoginModel();
			loginModel.setEmailAddress(username);
			loginModel.setPassword(password);

			if(userService.validateCredentials(loginModel)){


				UserModel userModel = userService.getUserByEmailAddress(loginModel.getEmailAddress());

				// Add all scopes here
				Set<String> scopes = new HashSet<>(Arrays.asList(new String[]{"offline"}));

				String authorizationCode = oAuthService.createAuthorizationCode(application, userModel, scopes);

				String bearerToken = oAuthService.getBearerTokenFromAuthorizationCode(authorizationCode, clientId, clientSecret);
				String scope = StringUtils.join(scopes, ",");

				OAuthTokenModel tokenModel = new OAuthTokenModel();
				tokenModel.setAccessToken(bearerToken);
				tokenModel.setTokenType("bearer");
				tokenModel.setExpiresIn(60 * 60);
				tokenModel.setScope(scope);

				return tokenModel;

			} else {
				throw new EntityNotPermittedException("Invalid Credentials");
			}
		}

		if(grantType.equals("external_bearer"))
		{
			String code = httpServletRequest.getParameter("code");
			UserModel userModel = oAuthService.getUserFromBearerToken(code);

			if(userModel != null){
				// Add all scopes here
				Set<String> scopes = oAuthService.getScopesFromBearerToken(code); // Analytics will have access to the same scopes as the original

				String authorizationCode = oAuthService.createAuthorizationCode(application, userModel, scopes);

				String bearerToken = oAuthService.getBearerTokenFromAuthorizationCode(authorizationCode, clientId, clientSecret);
				String scope = StringUtils.join(scopes, ",");

				OAuthTokenModel tokenModel = new OAuthTokenModel();
				tokenModel.setAccessToken(bearerToken);
				tokenModel.setTokenType("bearer");
				tokenModel.setExpiresIn(60 * 60);
				tokenModel.setScope(scope);

				return tokenModel;

			} else {
				throw new EntityNotPermittedException("Invalid Bearer Token");
			}
		}

		throw new EntityNotPermittedException("Invalid Token Request");
	}


}
