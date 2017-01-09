package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.DemandSource;

/**
 * Created by jhaygood on 1/27/16.
 */

@WebSafeModel
public class DemandSourceModel {

    @JsonProperty("demand_source_id")
    private int demandSourceId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("summary_impressions")
    private Long summaryImpressions;

    @JsonProperty("summary_fillrate")
    private Integer summaryFillRate;

    @JsonProperty("summary_ecpm")
    private Integer summaryECPM;

    @JsonProperty("summary_rcpm")
    private Integer summaryRCPM;

    @JsonProperty("sort_order")
    private Integer order;

    @JsonProperty("has_priority")
    private Boolean hasPriority;

    public DemandSourceModel(){
        setOrder(-1);
    }

    public DemandSourceModel(DemandSource demandSource){
        setName(demandSource.getName());
        setDemandSourceId(demandSource.getId());
        setSummaryImpressions(demandSource.getSummaryImpressions());
        setSummaryFillRate(demandSource.getSummaryFillRate());
        setSummaryECPM(demandSource.getSummaryECPM());
        setSummaryRCPM(demandSource.getSummaryRCPM());
        setOrder(-1);
    }

    public int getDemandSourceId() {
        return demandSourceId;
    }

    public void setDemandSourceId(int demandSourceId) {
        this.demandSourceId = demandSourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSummaryImpressions() {
        return summaryImpressions;
    }

    public Integer getSummaryFillRate() {
        return summaryFillRate;
    }

    public Integer getSummaryECPM() {
        return summaryECPM;
    }

    public Integer getSummaryRCPM() {
        return summaryRCPM;
    }

    public void setSummaryImpressions(Long summaryImpressions) {
        this.summaryImpressions = summaryImpressions;
    }

    public void setSummaryFillRate(Integer summaryFillRate) {
        this.summaryFillRate = summaryFillRate;
    }

    public void setSummaryECPM(Integer summaryECPM) {
        this.summaryECPM = summaryECPM;
    }

    public void setSummaryRCPM(Integer summaryRCPM) {
        this.summaryRCPM = summaryRCPM;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getHasPriority() {
        return hasPriority;
    }

    public void setHasPriority(Boolean hasPriority) {
        this.hasPriority = hasPriority;
    }
}
