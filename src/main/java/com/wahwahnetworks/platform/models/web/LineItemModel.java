package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.LineItem;
import com.wahwahnetworks.platform.data.entities.LineItemAd;
import com.wahwahnetworks.platform.data.entities.enums.LineItemAdWeightingMode;
import com.wahwahnetworks.platform.data.entities.enums.LineItemCPMType;
import com.wahwahnetworks.platform.data.entities.enums.LineItemDeliveryMode;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingCardinality;
import com.wahwahnetworks.platform.lib.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@WebSafeModel
public class LineItemModel
{

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("ads")
	private List<DemandSourceLineItemAdModel> ads;

	@JsonProperty("is_paused")
	private boolean paused;

	@JsonProperty("targeting")
	private List<LineItemTargetingModel>  targeting;

	@JsonProperty("supports_mobile")
	private Boolean supportsMobile;

	@JsonProperty("enable_manual_weighting")
	private boolean enableManualWeighting;

	@JsonProperty("delivery_mode")
	private LineItemDeliveryMode deliveryMode;

	@JsonProperty("cpm")
	private String cpm;

	@JsonProperty("cpm_type")
	private LineItemCPMType cpmType;

	@JsonProperty("demand_source_id")
	private Integer demandSourceId;

	@JsonProperty("demand_source_name")
	private String demandSourceName;

	@JsonProperty("targeting_cardinality")
	private LineItemTargetingCardinality targetingCardinality;

	public LineItemModel(){
		ads = new ArrayList<>();
	}

	public LineItemModel(LineItem lineItem)
	{
		this.ads = new ArrayList<>();

		this.setId(lineItem.getId());
		this.setName(lineItem.getName());
		this.setPaused(lineItem.isArchived());
		this.setTargetingCardinality(lineItem.getTargetingCardinality());

		switch(lineItem.getAdWeightingMode()){
			case EQUAL:
				this.setEnableManualWeighting(false);
				break;
			case MANUAL:
				this.setEnableManualWeighting(true);
				break;
		}

		this.setDeliveryMode(lineItem.getDeliveryMode());

		this.setCpmType(lineItem.getCpmType());

		this.setDemandSourceId(lineItem.getDemandSource().getId());

		this.setDemandSourceName(lineItem.getDemandSource().getName());

		if(lineItem.getExpectedCPMInCents() != null) {

			this.setCpm(MoneyUtils.getDollarStringForCents(lineItem.getExpectedCPMInCents()));
		}

		if(lineItem.getAdWeightingMode() == LineItemAdWeightingMode.MANUAL){
			this.setEnableManualWeighting(true);
		}

		for(LineItemAd lineItemAd : lineItem.getAds()){
			DemandSourceLineItemAdModel lineItemAdModel = new DemandSourceLineItemAdModel();
			lineItemAdModel.setId(lineItemAd.getId());
			lineItemAdModel.setName(lineItemAd.getName());
			lineItemAdModel.setWeightPercentage(lineItemAd.getWeightPercentage());
			lineItemAdModel.setPaused(lineItemAd.isArchived());

			Creative creative = lineItemAd.getCreative();

			lineItemAdModel.setCreativeId(creative.getId());

			this.getAds().add(lineItemAdModel);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<DemandSourceLineItemAdModel> getAds()
	{
		return ads;
	}

	public void setAds(List<DemandSourceLineItemAdModel> ads)
	{
		this.ads = ads;
	}

	public boolean isPaused()
	{
		return paused;
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getSupportsMobile()
	{
		return supportsMobile;
	}

	public void setSupportsMobile(Boolean supportsMobile)
	{
		this.supportsMobile = supportsMobile;
	}

	public List<LineItemTargetingModel> getTargeting()
	{
		return targeting;
	}

	public void setTargeting(List<LineItemTargetingModel> targeting)
	{
		this.targeting = targeting;
	}

    public boolean isEnableManualWeighting() {
        return enableManualWeighting;
    }

    public void setEnableManualWeighting(boolean enableManualWeighting) {
        this.enableManualWeighting = enableManualWeighting;
    }

    public LineItemDeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(LineItemDeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getCpm() {
        return cpm;
    }

    public void setCpm(String cpm) {
        this.cpm = cpm;
    }

    public LineItemCPMType getCpmType() {
        return cpmType;
    }

    public void setCpmType(LineItemCPMType cpmType) {
        this.cpmType = cpmType;
    }

	public Integer getDemandSourceId()
	{
		return demandSourceId;
	}

	public void setDemandSourceId(Integer demandSourceId)
	{
		this.demandSourceId = demandSourceId;
	}

	public String getDemandSourceName()
	{
		return demandSourceName;
	}

	public void setDemandSourceName(String demandSourceName)
	{
		this.demandSourceName = demandSourceName;
	}

	public LineItemTargetingCardinality getTargetingCardinality()
	{
		return targetingCardinality;
	}

	public void setTargetingCardinality(LineItemTargetingCardinality targetingCardinality)
	{
		this.targetingCardinality = targetingCardinality;
	}
}
