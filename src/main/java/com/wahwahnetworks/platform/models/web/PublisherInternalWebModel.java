package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;
import com.wahwahnetworks.platform.models.BillableEntityModel;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
public class PublisherInternalWebModel extends PublisherWebModel implements BillableEntityModel
{
	@JsonProperty("internal_notes")
	private String internalNotes;

	public PublisherInternalWebModel()
	{
	}

	public PublisherInternalWebModel(AccountPublisher account, AccountPublisher redPandaPublisherCreator, int siteCount)
	{
		super(account, redPandaPublisherCreator, siteCount);
		setInternalNotes(account.getInternalNotes());
	}

	public String getInternalNotes()
	{
		return internalNotes;
	}

	public void setInternalNotes(String internalNotes)
	{
		this.internalNotes = internalNotes;
	}


	public Integer getBillableEntityTargetId(){
		return accountId;
	}

	public BillableEntityType getBillableEntityTargetType(){
		return BillableEntityType.PUBLISHER;
	}
}
