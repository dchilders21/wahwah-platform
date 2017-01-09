package com.wahwahnetworks.platform.models;

/**
 * Created by jhaygood on 3/2/15.
 */
public class OAuthGrantModel
{
	private String xsrfToken;
	private String clientId;
	private String redirectUri;
	private String responseType;
	private String scope;
	private String state;
	private Boolean doGrant;

	public String getXsrfToken()
	{
		return xsrfToken;
	}

	public void setXsrfToken(String xsrfToken)
	{
		this.xsrfToken = xsrfToken;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getRedirectUri()
	{
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri)
	{
		this.redirectUri = redirectUri;
	}

	public String getResponseType()
	{
		return responseType;
	}

	public void setResponseType(String responseType)
	{
		this.responseType = responseType;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public Boolean getDoGrant()
	{
		return doGrant;
	}

	public void setDoGrant(Boolean doGrant)
	{
		this.doGrant = doGrant;
	}
}
