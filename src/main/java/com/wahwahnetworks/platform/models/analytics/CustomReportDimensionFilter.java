package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 4/18/16.
 */
public class CustomReportDimensionFilter {

    @JsonProperty("dimension")
    private String dimension;

    @JsonProperty("value")
    private String filterValue;

    @JsonProperty("filter_type")
    private CustomReportDimensionFilterType filterType;

    public CustomReportDimensionFilter(){
        setFilterType(CustomReportDimensionFilterType.INCLUDE);
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public CustomReportDimensionFilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(CustomReportDimensionFilterType filterType) {
        this.filterType = filterType;
    }

    public static CustomReportDimensionFilter of(String dimension, String value){
        CustomReportDimensionFilter customReportDimensionFilter = new CustomReportDimensionFilter();
        customReportDimensionFilter.setDimension(dimension);
        customReportDimensionFilter.setFilterValue(value);
        return customReportDimensionFilter;
    }

    public static CustomReportDimensionFilter of(String dimension, String value, CustomReportDimensionFilterType filterType){
        CustomReportDimensionFilter customReportDimensionFilter = of(dimension, value);
        customReportDimensionFilter.setFilterType(filterType);
        return customReportDimensionFilter;
    }
}