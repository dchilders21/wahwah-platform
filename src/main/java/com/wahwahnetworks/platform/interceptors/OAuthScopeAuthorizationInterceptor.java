package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.OAuthAllowScope;
import com.wahwahnetworks.platform.services.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Created by jhaygood on 3/3/15.
 */

@Component
public class OAuthScopeAuthorizationInterceptor implements HandlerInterceptor
{

	@Autowired
	private OAuthService oAuthService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{

		Boolean isOAuthSession = (Boolean) request.getSession().getAttribute("oauth_authenticated_session");

		if (isOAuthSession == null || isOAuthSession == false)
		{ // Not an OAuth Session -- so allow the request
			return true;
		}

		if (handler instanceof HandlerMethod)
		{

			HandlerMethod handlerMethod = (HandlerMethod) handler;
			OAuthAllowScope allowScopeAnnotation = handlerMethod.getMethodAnnotation(OAuthAllowScope.class);

			// No annotation means the endpoint cannot be used in an OAuth request
			if (allowScopeAnnotation == null)
			{
				return false;
			}

			String bearerToken = (String) request.getSession().getAttribute("oauth_bearer_token");
			Set<String> scopesForBearer = oAuthService.getScopesFromBearerToken(bearerToken);

			String[] scopes = allowScopeAnnotation.value();

			for(String scope : scopes) {
				if (scopesForBearer.contains(scope)) {
					return true;
				}
			}

			// return HTTP 403 Forbidden if we failed all previous checks
			response.setStatus(403);

			return false;
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
