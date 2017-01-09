package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 4/18/16.
 */
public class CustomReportResultCSVModel extends CustomReportResponseModel {

    @JsonProperty("csv_result")
    private String resultAsCsv;

    public CustomReportResultCSVModel(){
        setSuccessful(true);
        setCustomReportResponseType(CustomReportResponseType.SUCCESS);
    }

    public String getResultAsCsv() {
        return resultAsCsv;
    }

    public void setResultAsCsv(String resultAsCsv) {
        this.resultAsCsv = resultAsCsv;
    }
}