package com.wahwahnetworks.platform.models.tie;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.data.entities.enums.LineItemAdWeightingMode;
import com.wahwahnetworks.platform.data.entities.enums.LineItemDeliveryMode;

import java.util.List;

/**
 * Created by jhaygood on 6/7/16.
 */
public class TagIntelligenceLineItemModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("ads")
    private List<String> ads;

    @JsonProperty("weighting_mode")
    private LineItemAdWeightingMode adWeightingMode;

    @JsonProperty("delivery_mode")
    private LineItemDeliveryMode deliveryMode;

    @JsonProperty("nominal_cpm")
    private Integer nominalCPM;

    @JsonProperty("actual_rcpm")
    private Integer actualRCPM;

    @JsonProperty("actual_ecpm")
    private Integer actualECPM;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAds() {
        return ads;
    }

    public void setAds(List<String> ads) {
        this.ads = ads;
    }

    public LineItemAdWeightingMode getAdWeightingMode() {
        return adWeightingMode;
    }

    public void setAdWeightingMode(LineItemAdWeightingMode adWeightingMode) {
        this.adWeightingMode = adWeightingMode;
    }

    public LineItemDeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(LineItemDeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public Integer getNominalCPM() {
        return nominalCPM;
    }

    public void setNominalCPM(Integer nominalCPM) {
        this.nominalCPM = nominalCPM;
    }

    public Integer getActualRCPM() {
        return actualRCPM;
    }

    public void setActualRCPM(Integer actualRCPM) {
        this.actualRCPM = actualRCPM;
    }

    public Integer getActualECPM() {
        return actualECPM;
    }

    public void setActualECPM(Integer actualECPM) {
        this.actualECPM = actualECPM;
    }
}
