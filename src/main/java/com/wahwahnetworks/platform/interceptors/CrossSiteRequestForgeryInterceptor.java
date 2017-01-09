package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.lib.HMacUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jhaygood on 3/2/15.
 */

@Component
public class CrossSiteRequestForgeryInterceptor implements HandlerInterceptor
{

	private static final String XSRF_KEY = "switcheroo"; // Dictionary.com WOTD for 2015-03-01

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{

		String sessionId = request.getSession().getId();
		String hmac_session = HMacUtil.hmac(sessionId, XSRF_KEY);
		request.getSession().setAttribute("xsrf_token", hmac_session);

		Boolean isRestAuthenticatedSession = (Boolean) request.getSession().getAttribute("rest_authentication_session");

		if (handler instanceof HandlerMethod)
		{

			HandlerMethod handlerMethod = (HandlerMethod) handler;

			boolean isRestController = handlerMethod.getBean().getClass().getAnnotation(RestController.class) != null;

			if (isRestController)
			{

				if (isRestAuthenticatedSession)
				{
					// Request has been authorized separately, so not using cookies for auth
					return true;
				}

				String xsrfToken = request.getHeader("X-XSRF-TOKEN");

				if (xsrfToken == null)
				{
					if (request.getParameterMap().containsKey("xsrf_token"))
					{
						xsrfToken = request.getParameterMap().get("xsrf_token")[0];
					}
				}

				if (xsrfToken != null && xsrfToken.equals(hmac_session))
				{
					return true;
				}
				else
				{

					if (isRestController)
					{

						boolean bypassAuthentication = handlerMethod.getBean().getClass().getAnnotation(RestNoAuthentication.class) != null;

						if (bypassAuthentication)
						{
							return true;
						}
					}

					response.setStatus(401);
					return false;
				}
			}

		}

		if (request.getMethod().equals("GET"))
		{
			boolean hasXsrfTokenCookie = false;

			if (request.getCookies() != null)
			{
				for (Cookie cookie : request.getCookies())
				{
					if (cookie.getName().equals("XSRF-TOKEN") && cookie.getValue().equals(hmac_session))
					{
						hasXsrfTokenCookie = true;
					}
				}
			}

			if (!hasXsrfTokenCookie)
			{
				Cookie cookie = new Cookie("XSRF-TOKEN", hmac_session);
				cookie.setHttpOnly(false);
				cookie.setPath(request.getContextPath());

				response.addCookie(cookie);
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
