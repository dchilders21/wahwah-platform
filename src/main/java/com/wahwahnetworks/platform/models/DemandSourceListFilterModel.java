package com.wahwahnetworks.platform.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.models.analytics.CustomReportDimensionFilter;
import com.wahwahnetworks.platform.models.analytics.CustomReportRequestModel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jhaygood on 4/20/16.
 */
public class DemandSourceListFilterModel {
    @JsonProperty("dimension_filters")
    private List<CustomReportDimensionFilter> filterDimensions;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("show_inactive")
    private boolean showInactive;

    public List<CustomReportDimensionFilter> getFilterDimensions() {
        return filterDimensions;
    }

    public void setFilterDimensions(List<CustomReportDimensionFilter> filterDimensions) {
        this.filterDimensions = filterDimensions;
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

    public CustomReportRequestModel toReportRequestModel(){
        CustomReportRequestModel reportRequestModel = new CustomReportRequestModel();
        reportRequestModel.setFilterDimensions(getFilterDimensions());
        reportRequestModel.setStartDate(startDate);
        reportRequestModel.setEndDate(endDate);

        reportRequestModel.setMetrics(Arrays.asList("Impressions","Fill Rate","eCPM","rCPM"));
        reportRequestModel.setGroupDimensions(Arrays.asList("Demand Source","Demand Source ID"));

        return reportRequestModel;
    }

    public boolean isShowInactive() {
        return showInactive;
    }

    public void setShowInactive(boolean showInactive) {
        this.showInactive = showInactive;
    }
}
