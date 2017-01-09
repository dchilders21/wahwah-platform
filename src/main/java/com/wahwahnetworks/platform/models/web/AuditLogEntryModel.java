package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.time.OffsetDateTime;

/**
 * Created by Justin on 1/26/2015.
 */

@WebSafeModel
public class AuditLogEntryModel
{
	private String actionType;
	private OffsetDateTime entryTime;
	private String description;
	private String comment;
	private String userName;
	private int userId;
	private String accountName;
	private int accountId;

	public String getActionType()
	{
		return actionType;
	}

	public void setActionType(String actionType)
	{
		this.actionType = actionType;
	}

	public OffsetDateTime getEntryTime()
	{
		return entryTime;
	}

	public void setEntryTime(OffsetDateTime entryTime)
	{
		this.entryTime = entryTime;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}
}
