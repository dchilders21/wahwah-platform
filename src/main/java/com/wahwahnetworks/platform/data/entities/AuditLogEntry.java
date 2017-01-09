package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by Justin on 1/1/2015.
 */

@Entity
@Table(name = "audit_log")
public class AuditLogEntry
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "action_type_id", nullable = false)
	private AuditActionType auditActionType;

	@Column(name = "description")
	private String description;

	@Column(name = "comment_msg")
	private String commentMessage;

	@Column(name = "user_email_address")
	private String userEmailAddress;

	@Column(name = "account_name")
	private String accountName;

	@Column(name = "entry_time")
	private Instant entryTime;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = true)
	private Account account;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public AuditActionType getActionType()
	{
		return auditActionType;
	}

	public void setActionType(AuditActionType actionType)
	{
		this.auditActionType = actionType;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCommentMessage()
	{
		return commentMessage;
	}

	public void setCommentMessage(String commentMessage)
	{
		this.commentMessage = commentMessage;
	}

	public String getUserName()
	{
		if (user != null)
		{
			return user.getFirstName() + " " + user.getLastName();
		}
		else
		{
			return userEmailAddress;
		}
	}

	public String getUserEmailAddress()
	{
		if (user != null)
		{
			return user.getEmailAddress();
		}
		else
		{
			return userEmailAddress;
		}
	}

	public void setUser(User user)
	{
		userEmailAddress = user.getEmailAddress();
		this.user = user;
	}

	public String getAccountName()
	{
		if (account != null)
		{
			return account.getName();
		}
		else
		{
			return accountName;
		}
	}

	public void setAccount(Account account)
	{
		this.account = account;

		if (account != null)
		{
			this.accountName = account.getName();
		}
	}

	public Instant getEntryTime()
	{
		return entryTime;
	}

	public void setEntryTime(Instant entryTime)
	{
		this.entryTime = entryTime;
	}
}
