package com.wahwahnetworks.platform.interceptors;

import com.wahwahnetworks.platform.annotations.WebSafeModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Justin on 5/17/2014.
 */

@Component
public class WebSafeRestInterceptor implements HandlerInterceptor
{
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception
	{
		if (handler instanceof HandlerMethod)
		{

			HandlerMethod handlerMethod = (HandlerMethod) handler;

			boolean isRestController = handlerMethod.getBean().getClass().getAnnotation(RestController.class) != null;

			if (isRestController)
			{
				boolean isWebSafeModel = handlerMethod.getMethod().getReturnType().getAnnotation(WebSafeModel.class) != null;

				if (handlerMethod.getMethod().getReturnType().equals(String.class))
				{
					isWebSafeModel = true;
				}

				if (isWebSafeModel)
				{
					return true;
				}
				else
				{

					boolean isVoidReturn = handlerMethod.getMethod().getReturnType().equals(Void.TYPE);

					if (isVoidReturn)
					{
						ResponseStatus responseStatus = handlerMethod.getMethodAnnotation(ResponseStatus.class);

						if (responseStatus != null)
						{
							if (responseStatus.value() == HttpStatus.NO_CONTENT)
							{
								return true;
							}

							if (responseStatus.value() == HttpStatus.CREATED)
							{
								return true;
							}
						}
					}

					throw new Exception("RestController response is not web-safe");
				}
			}
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
