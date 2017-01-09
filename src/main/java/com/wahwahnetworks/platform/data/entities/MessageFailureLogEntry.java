package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by Brian.Bober on 4/26/2016.
 */

@Entity
@Table(name = "message_failure_log")
public class MessageFailureLogEntry
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "uid")
	private String uid;

	@Column(name = "msg_name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "entry_time")
	private Instant entryTime;

	@Column(name = "entry_time_last")
	private Instant entryTimeLastOccurrence;

	@Column(name = "entry_time_resolved")
	private Instant entryTimeResolved;

	@Column(name = "failure_count")
	private Integer failureCount;

	@Column(name = "resolved")
	private Boolean resolved;

	@Column(name = "latest_delay_milli")
	private int latestDelayInMillis;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Instant getEntryTime()
	{
		return entryTime;
	}

	public void setEntryTime(Instant entryTime)
	{
		this.entryTime = entryTime;
	}

	public Integer getFailureCount()
	{
		return failureCount;
	}

	public void setFailureCount(Integer failureCount)
	{
		this.failureCount = failureCount;
	}

	public Boolean getResolved()
	{
		return resolved;
	}

	public void setResolved(Boolean resolved)
	{
		this.resolved = resolved;
	}

	public Instant getEntryTimeLastOccurrence()
	{
		return entryTimeLastOccurrence;
	}

	public void setEntryTimeLastOccurrence(Instant entryTimeLastOccurrence)
	{
		this.entryTimeLastOccurrence = entryTimeLastOccurrence;
	}

	public Instant getEntryTimeResolved()
	{
		return entryTimeResolved;
	}

	public void setEntryTimeResolved(Instant entryTimeResolved)
	{
		this.entryTimeResolved = entryTimeResolved;
	}

	public int getLatestDelayInMillis()
	{
		return latestDelayInMillis;
	}

	public void setLatestDelayInMillis(int latestDelayInMillis)
	{
		this.latestDelayInMillis = latestDelayInMillis;
	}
}
