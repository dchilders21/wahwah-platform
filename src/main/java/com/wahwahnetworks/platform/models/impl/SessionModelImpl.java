package com.wahwahnetworks.platform.models.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Justin on 5/17/2014.
 */
@Component
@Scope("session")
public class SessionModelImpl implements Serializable
{

	private int userId = 0;

	private int realUserId = 0; // Impersonation/Masquerading

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public void setRealUserId(int userId)
	{
		this.realUserId = userId;
	}

	public int getUserId()
	{
		return userId;
	}

	public int getRealUserId()
	{
		if (realUserId != 0)
			return realUserId;
		else
			return userId;
	}

}