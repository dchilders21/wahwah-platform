package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@WebSafeModel
public class DemandSourceManagementModel
{

	@JsonProperty("demand_source")
	public DemandSourceModel demandSourceModel;

	@JsonProperty("account")
    private AccountWebModel accountWebModel;

	@JsonProperty("connections")
	private List<DemandSourceConnectionModel> connections;

	@JsonProperty("line_items")
	private List<LineItemModel> lineItems;

	@JsonProperty("creatives")
	private List<DemandSourceCreativeModel> creatives;

	@JsonProperty("placements")
	private List<DemandSourcePlacementModel> placements;

	public DemandSourceModel getDemandSourceModel()
	{
		return demandSourceModel;
	}

	public void setDemandSourceModel(DemandSourceModel demandSourceModel)
	{
		this.demandSourceModel = demandSourceModel;
	}

	public List<LineItemModel> getLineItems()
	{
		return lineItems;
	}

	public void setLineItems(List<LineItemModel> lineItems)
	{
		this.lineItems = lineItems;
	}

	public List<DemandSourceConnectionModel> getConnections()
	{
		return connections;
	}

	public void setConnections(List<DemandSourceConnectionModel> connections)
	{
		this.connections = connections;
	}

    public List<DemandSourceCreativeModel> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<DemandSourceCreativeModel> creatives) {
        this.creatives = creatives;
    }

	public List<DemandSourcePlacementModel> getPlacements() {
		return placements;
	}

	public void setPlacements(List<DemandSourcePlacementModel> placements) {
		this.placements = placements;
	}

    public AccountWebModel getAccountWebModel() {
        return accountWebModel;
    }

    public void setAccountWebModel(AccountWebModel accountWebModel) {
        this.accountWebModel = accountWebModel;
    }
}
