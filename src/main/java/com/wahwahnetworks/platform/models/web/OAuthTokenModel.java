package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by jhaygood on 3/3/15.
 */

@WebSafeModel
public class OAuthTokenModel
{

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private Integer expiresIn;

	@JsonProperty("scope")
	private String scope;

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}

	public String getTokenType()
	{
		return tokenType;
	}

	public void setTokenType(String tokenType)
	{
		this.tokenType = tokenType;
	}

	public Integer getExpiresIn()
	{
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn)
	{
		this.expiresIn = expiresIn;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}
}
