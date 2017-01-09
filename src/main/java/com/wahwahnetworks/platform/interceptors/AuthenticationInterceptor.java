package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.controllers.LoginController;
import com.wahwahnetworks.platform.controllers.OAuthController;
import com.wahwahnetworks.platform.lib.HMacUtil;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Justin on 3/25/2014.
 */

@Transactional
@Component
public class AuthenticationInterceptor implements HandlerInterceptor
{

	@Autowired
	private UserService userService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException
	{
		if (handler instanceof HandlerMethod)
		{
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			if (!(handlerMethod.getBean() instanceof LoginController))
			{

				if (handlerMethod.getBean() instanceof OAuthController)
				{
					if (handlerMethod.getMethod().getName().equals("getBearerToken"))
					{
						return true;
					}
				}

				Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("logged_in");
				Integer userId = (Integer) request.getSession().getAttribute("user_id");

				if (isLoggedIn == null || userId == null || isLoggedIn == false)
				{

					boolean isRestController = handlerMethod.getBean().getClass().getAnnotation(RestController.class) != null;

					if (isRestController)
					{

						boolean bypassAuthenticationOnClass = handlerMethod.getBean().getClass().getAnnotation(RestNoAuthentication.class) != null;
						boolean bypassAuthenticationOnMethod = handlerMethod.getMethodAnnotation(RestNoAuthentication.class) != null;

						boolean bypassAuthentication = bypassAuthenticationOnClass || bypassAuthenticationOnMethod;

						if (bypassAuthentication)
						{
							request.getSession().setAttribute("rest_authentication_session",true);
							return true;
						}

						response.setStatus(401);
					}
					else
					{


						if (request.getCookies() != null)
						{
							for (Cookie cookie : request.getCookies())
							{
								if (cookie.getName().equals("WAHWAH_REMEMBERME_TOKEN"))
								{

									if (isLoggedIn != null && isLoggedIn == false)
									{
										break;
									}

									UserModel userModel = userService.getUserFromRememberMeCookieValue(cookie.getValue());
									if (userModel != null)
									{
										request.getSession().setAttribute("logged_in", true);
										request.getSession().setAttribute("user_id", userModel.getUserId());
										request.getSession().setAttribute("real_user_id", userModel.getUserId());

										try
										{
											Cookie newCookie = new Cookie("WAHWAH_REMEMBERME_TOKEN", userService.getRememberMeCookieValue(userModel));
											newCookie.setMaxAge(604800); // 1 Week In Seconds
											newCookie.setPath(request.getContextPath());
											newCookie.setHttpOnly(true);

											response.addCookie(cookie);

										}
										catch (Exception exception)
										{
										}

										return true;
									}
								}
							}
						}

						if (request.getMethod().equals("GET"))
						{
							String requestUri = request.getServletPath();

							if (request.getQueryString() != null && !request.getQueryString().isEmpty())
							{
								requestUri = requestUri + "?" + request.getQueryString();
							}
							request.getSession().setAttribute("redirect_uri", requestUri);
						}

						response.sendRedirect(request.getContextPath().concat("/useraccount/login"));
					}

					return false;
				}
				else
				{
					boolean hasWahwahAppToken = false;

					String sessionId = request.getSession().getId();
					String hmac_user_session = HMacUtil.hmac(userId.toString(), sessionId);

					if (request.getCookies() != null)
					{
						for (Cookie cookie : request.getCookies())
						{
							if (cookie.getName().equals("wahwah_app_token") && cookie.getValue().equals(hmac_user_session))
							{
								hasWahwahAppToken = true;
							}
						}
					}

					if (!hasWahwahAppToken)
					{
						Cookie cookie = new Cookie("wahwah_app_token", hmac_user_session);
						cookie.setHttpOnly(false);
						cookie.setPath(request.getContextPath());

						response.addCookie(cookie);
					}
				}
			}
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
	{

	}
}
