package com.wahwahnetworks.platform.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 4/20/16.
 */
public class DemandSourcePriorityModel {
    @JsonProperty("demand_source_id")
    private int demandSourceId;

    @JsonProperty("priority")
    private int priority;

    public int getDemandSourceId() {
        return demandSourceId;
    }

    public void setDemandSourceId(int demandSourceId) {
        this.demandSourceId = demandSourceId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
