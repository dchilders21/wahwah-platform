package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 3/2/15.
 */

@Entity
@Table(name = "oauth_grants")
public class OAuthGrant
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "application_id")
	private OAuthApplication application;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public OAuthApplication getApplication()
	{
		return application;
	}

	public void setApplication(OAuthApplication application)
	{
		this.application = application;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
}