package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@WebSafeModel
public class DemandSourceLineItemAdModel
{
	@JsonProperty("id")
	private Integer id;

	@JsonProperty("creative_id")
	private Integer creativeId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("weight_percentage")
	private Integer weightPercentage;

	@JsonProperty("is_paused")
	private boolean paused;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCreativeId() {
		return creativeId;
	}

	public void setCreativeId(Integer creativeId) {
		this.creativeId = creativeId;
	}

	public Integer getWeightPercentage() {
		return weightPercentage;
	}

	public void setWeightPercentage(Integer weightPercentage) {
		this.weightPercentage = weightPercentage;
	}

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
