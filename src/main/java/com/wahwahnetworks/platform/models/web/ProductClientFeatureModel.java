package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 11/15/15.
 */
public class ProductClientFeatureModel {
    @JsonProperty("id")
    private int id;

    @JsonProperty("feature_id")
    private int featureId;

    @JsonProperty("value_string")
    private String valueString;

    @JsonProperty("value_number")
    private Double valueNumber;

    @JsonProperty("value_boolean")
    private Boolean valueBoolean;

    @JsonProperty("feature")
    private ClientFeatureWebModel clientFeature;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Double getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(Double valueNumber) {
        this.valueNumber = valueNumber;
    }

    public Boolean getValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(Boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public ClientFeatureWebModel getClientFeature() {
        return clientFeature;
    }

    public void setClientFeature(ClientFeatureWebModel clientFeature) {
        this.clientFeature = clientFeature;
    }
}
