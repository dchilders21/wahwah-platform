package com.wahwahnetworks.platform.models.rabbitmq;

import com.wahwahnetworks.platform.services.rabbit.DelayedExchangeService;

import java.text.SimpleDateFormat;

/**
 * Created by Brian.Bober on 4/26/2016.
 */

public abstract class RMQDelayBase implements RMQModel
{
	private Integer delay = DelayedExchangeService.getInitialDelay();
	private String uid = DelayedExchangeService.generateMessageUUID(); // Used for avoiding duplicates in db message_failure table
	private String startTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis());

	public Integer getBackoffDelay()
	{
		return delay;
	}

	public void setBackoffDelay(Integer delay)
	{
		this.delay = delay;
	}

	public boolean success = false; // Used for database tracking of errors

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
}
