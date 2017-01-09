package com.wahwahnetworks.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Justin on 12/26/2014.
 */

@Component
public class WebConfiguration extends WebMvcConfigurerAdapter
{

	@Resource(name = "authorizationInterceptor")
	private HandlerInterceptor authorizationInterceptor;

	@Resource(name = "authenticationInterceptor")
	private HandlerInterceptor authenticationInterceptor;

	@Resource(name = "restAuthenticationInterceptor")
	private HandlerInterceptor restAuthenticationInterceptor;

	@Resource(name = "webSafeRestInterceptor")
	private HandlerInterceptor webSafeRestInterceptor;

	@Resource(name = "crossSiteRequestForgeryInterceptor")
	private HandlerInterceptor crossSiteRequestForgeryInterceptor;

	@Resource(name = "OAuthScopeAuthorizationInterceptor")
	private HandlerInterceptor oauthScopeAuthorizationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(restAuthenticationInterceptor);
		registry.addInterceptor(authenticationInterceptor);
		registry.addInterceptor(authorizationInterceptor);
		registry.addInterceptor(webSafeRestInterceptor);
		registry.addInterceptor(crossSiteRequestForgeryInterceptor);
		registry.addInterceptor(oauthScopeAuthorizationInterceptor);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("/components/**").addResourceLocations("/components/");
		registry.addResourceHandler("/content/**").addResourceLocations("/content/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/views/**").addResourceLocations("/views/");
		registry.addResourceHandler("/error/content/**").addResourceLocations("/content/");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				AbstractJackson2HttpMessageConverter c = (AbstractJackson2HttpMessageConverter) converter;
				ObjectMapper objectMapper = c.getObjectMapper();
				objectMapper.registerModule(new KotlinModule());
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
			}
		}

		super.extendMessageConverters(converters);
	}
}
