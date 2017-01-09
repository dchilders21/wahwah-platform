package com.wahwahnetworks.platform.models.rabbitmq;

/**
 * Created by Brian.Bober on 4/26/2016.
 */
// Only add items as-needed.
// Important! Make sure to null-check anything added if there are older messages in queue
public class NetworkRMQModel extends RMQDelayBase
{
	private Integer networkId = null;
	private String redPandaName = null;
	private Integer redPandaId = null;
	private String notes = "";

	public Integer getNetworkId()
	{
		return networkId;
	}

	public void setNetworkId(Integer networkId)
	{
		this.networkId = networkId;
	}

	public String getRedPandaName()
	{
		return redPandaName;
	}

	public void setRedPandaName(String redPandaName)
	{
		this.redPandaName = redPandaName;
	}

	public Integer getRedPandaId()
	{
		return redPandaId;
	}

	public void setRedPandaId(Integer redPandaId)
	{
		this.redPandaId = redPandaId;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}
}