package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;
import com.wahwahnetworks.platform.models.BillableEntityModel;

@WebSafeModel
public class SiteInternalModel extends SiteModel implements BillableEntityModel
{
	@JsonProperty("internal_notes")
	private String internalNotes;

	public SiteInternalModel(Site site)
	{
		super(site);
		setInternalNotes(site.getInternalNotes());
    }

	public SiteInternalModel()
	{
	}

	public String getInternalNotes()
	{
		return internalNotes;
	}

	public void setInternalNotes(String internalNotes)
	{
		this.internalNotes = internalNotes;
	}

	@Override
	public String toString()
	{
		return super.toString();
	}

    @Override
    public BillableEntityType getBillableEntityTargetType() {
        return BillableEntityType.SITE;
    }

    @Override
    public Integer getBillableEntityTargetId() {
        return getId();
    }
}
