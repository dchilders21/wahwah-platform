package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;
import com.wahwahnetworks.platform.models.BillableEntityModel;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
public class NetworkInternalWebModel extends NetworkWebModel implements BillableEntityModel
{
	@JsonProperty("internal_notes")
	private String internalNotes;

	public NetworkInternalWebModel()
	{

	}

	public NetworkInternalWebModel(AccountNetwork account)
	{
		super(account);
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
        return BillableEntityType.NETWORK;
    }
}
