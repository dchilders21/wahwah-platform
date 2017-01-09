package com.wahwahnetworks.platform.models.rabbitmq;

/**
 * Created by Brian.Bober on 3/24/2016.
 */
// Only add items as-needed.
// Important! Make sure to null-check anything added if there are older messages in queue
public class PublisherRMQModel extends RMQDelayBase
{
	private String redPandaName = null;
	private Integer redPandaId = null;
	private String notes = "";

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