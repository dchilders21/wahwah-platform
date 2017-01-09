package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Account;

@WebSafeModel
public class AccountInternalWebModel extends AccountWebModel
{
	@JsonProperty("internal_notes")
	private String internalNotes;

	public AccountInternalWebModel()
	{
	}

	public AccountInternalWebModel(Account account)
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
}
