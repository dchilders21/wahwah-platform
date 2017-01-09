package com.wahwahnetworks.platform.models.web;

/**
 * Created by Brian.Bober on 5/12/2015.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.ArrayList;
import java.util.List;

@WebSafeModel
public class AdUnitListModel
{
	@JsonProperty("adUnits")
	private List<AdUnitModel> adUnitModels;

	public AdUnitListModel()
	{
		adUnitModels = new ArrayList<AdUnitModel>();
	}

	public List<AdUnitModel> getAdUnits()
	{
		return adUnitModels;
	}

	public void add(AdUnitModel unit)
	{
		adUnitModels.add(unit);
	}
}