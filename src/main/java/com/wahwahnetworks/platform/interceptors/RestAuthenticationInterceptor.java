package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.models.LoginModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.services.OAuthService;
import com.wahwahnetworks.platform.services.UserService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Justin on 5/19/2014.
 */

@Transactional
@Component
public class RestAuthenticationInterceptor implements HandlerInterceptor
{

	@Autowired
	private UserService userService;

	@Autowired
	private OAuthService oAuthService;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception
	{

		httpServletRequest.getSession().setAttribute("rest_authentication_session", false);

		if (handler instanceof HandlerMethod)
		{

			HandlerMethod handlerMethod = (HandlerMethod) handler;

			boolean isRestController = handlerMethod.getBean().getClass().getAnnotation(RestController.class) != null;

			if (isRestController)
			{

				String authHeader = httpServletRequest.getHeader("Authorization");

				if (authHeader != null && authHeader.startsWith("Basic "))
				{

					authHeader = authHeader.replace("Basic ", "");

					byte[] authHeaderBytes = Base64.decodeBase64(authHeader);
					String authHeaderDecoded = new String(authHeaderBytes);

					int firstColon = authHeaderDecoded.indexOf(':');

					String username = authHeaderDecoded.substring(0, firstColon);
					String password = authHeaderDecoded.substring(firstColon + 1);

					LoginModel loginModel = new LoginModel();
					loginModel.setEmailAddress(username);
					loginModel.setPassword(password);

					if (userService.validateCredentials(loginModel))
					{
						UserModel userModel = userService.getUserByEmailAddress(username);
						httpServletRequest.getSession().setAttribute("logged_in", true);
						httpServletRequest.getSession().setAttribute("user_id", userModel.getUserId());
						httpServletRequest.getSession().setAttribute("real_user_id", userModel.getUserId());
						httpServletRequest.getSession().setAttribute("rest_authentication_session", true);
					}
				}

				if (authHeader != null && authHeader.startsWith("Bearer "))
				{

					String bearerToken = authHeader.replace("Bearer ", "");
					UserModel userModel = oAuthService.getUserFromBearerToken(bearerToken);

					if (userModel != null)
					{
						httpServletRequest.getSession().setAttribute("logged_in", true);
						httpServletRequest.getSession().setAttribute("user_id", userModel.getUserId());
						httpServletRequest.getSession().setAttribute("real_user_id", userModel.getUserId());
						httpServletRequest.getSession().setAttribute("oauth_authenticated_session", true);
						httpServletRequest.getSession().setAttribute("oauth_bearer_token", bearerToken);
						httpServletRequest.getSession().setAttribute("rest_authentication_session", true);
					}
				}

			}

		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
	{

		Boolean isRestAuthenticatedSession = (Boolean) httpServletRequest.getSession().getAttribute("rest_authentication_session");

		if (isRestAuthenticatedSession != null && isRestAuthenticatedSession == true)
		{
			httpServletRequest.getSession().invalidate();
		}

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
	{

	}
}
