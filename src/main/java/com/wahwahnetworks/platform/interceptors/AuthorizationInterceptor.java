package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.services.UserService;
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
 * Created by Justin on 5/17/2014.
 */

@Transactional
@Component
public class AuthorizationInterceptor implements HandlerInterceptor
{

	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		if (handler instanceof HandlerMethod)
		{

			HandlerMethod handlerMethod = (HandlerMethod) handler;
			HasUserRole hasUserRoleAnnotation = handlerMethod.getMethodAnnotation(HasUserRole.class);

			// No annotation means the endpoint can be used user regardless of role
			if (hasUserRoleAnnotation == null)
			{
				return true;
			}

			Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("logged_in");
			Integer userId = (Integer) request.getSession().getAttribute("user_id");

			if (isLoggedIn != null && isLoggedIn && userId != null)
			{
				UserModel userModel = userService.getUserById(userId);

				// User with SUPER_USER role can do everything
				if (userModel.hasRole(UserRoleType.SUPER_USER))
				{
					return true;
				}

				// Of course, a user with the role specified can perform actions that require that role...


				for(UserRoleType roleType : hasUserRoleAnnotation.value()){
					if (userModel.hasRole(roleType)){
						return true;
					}
				}

			}

			boolean isRestController = handlerMethod.getBean().getClass().getAnnotation(RestController.class) != null;

			if (isRestController)
			{

				boolean bypassAuthentication = handlerMethod.getBean().getClass().getAnnotation(RestNoAuthentication.class) != null;

				if (bypassAuthentication)
				{
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
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
	{

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
	{

	}
}
