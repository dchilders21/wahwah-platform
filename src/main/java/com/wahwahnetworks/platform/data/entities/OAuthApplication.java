package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by jhaygood on 3/2/15.
 */

@Entity
@Table(name = "oauth_applications")
public class OAuthApplication
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "client_secret")
	private String clientSecret;

	@Column(name = "name")
	private String applicationName;

	@Column(name = "requires_consent")
	private Boolean requiresUserConsent;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "oauth_redirect_uris", joinColumns = @JoinColumn(name = "oauth_app_id"))
	@Column(name = "redirect_uri")
	private Set<String> redirectUris;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getClientSecret()
	{
		return clientSecret;
	}

	public void setClientSecret(String clientSecret)
	{
		this.clientSecret = clientSecret;
	}

	public String getApplicationName()
	{
		return applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public Boolean getRequiresUserConsent()
	{
		return requiresUserConsent;
	}

	public void setRequiresUserConsent(Boolean requiresUserConsent)
	{
		this.requiresUserConsent = requiresUserConsent;
	}

	public Set<String> getRedirectUris()
	{
		return redirectUris;
	}

	public void setRedirectUris(Set<String> redirectUris)
	{
		this.redirectUris = redirectUris;
	}
}
