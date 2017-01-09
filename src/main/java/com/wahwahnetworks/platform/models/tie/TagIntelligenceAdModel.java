package com.wahwahnetworks.platform.models.tie;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 6/7/16.
 */
public class TagIntelligenceAdModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("creative_id")
    private String creativeId;

    @JsonProperty("weight")
    private Integer weight;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
