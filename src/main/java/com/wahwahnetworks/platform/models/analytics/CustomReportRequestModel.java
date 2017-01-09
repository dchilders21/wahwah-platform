package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 4/18/16.
 */
public class CustomReportRequestModel {

    @JsonProperty("metrics")
    private List<String> metrics;

    @JsonProperty("dimension_groups")
    private List<String> groupDimensions;

    @JsonProperty("dimension_filters")
    private List<CustomReportDimensionFilter> filterDimensions;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    public CustomReportRequestModel(){
        metrics = new ArrayList<>();
        groupDimensions = new ArrayList<>();
        filterDimensions = new ArrayList<>();
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public List<String> getGroupDimensions() {
        return groupDimensions;
    }

    public void setGroupDimensions(List<String> groupDimensions) {
        this.groupDimensions = groupDimensions;
    }

    public List<CustomReportDimensionFilter> getFilterDimensions() {
        return filterDimensions;
    }

    public void setFilterDimensions(List<CustomReportDimensionFilter> filterDimensions) {
        this.filterDimensions = filterDimensions;
    }

    public void addFilter(CustomReportDimensionFilter filterDimension){
        filterDimensions.add(filterDimension);
    }

    public void addFilter(String dimension, String value){
        addFilter(CustomReportDimensionFilter.of(dimension,value));
    }

    public void addFilter(String dimension, String value, CustomReportDimensionFilterType filterType){
        addFilter(CustomReportDimensionFilter.of(dimension,value,filterType));
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}