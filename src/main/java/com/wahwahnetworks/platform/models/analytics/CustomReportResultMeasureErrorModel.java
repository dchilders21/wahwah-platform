package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 4/18/16.
 */
public class CustomReportResultMeasureErrorModel extends CustomReportResponseModel {

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("measure")
    private String measure;

    public CustomReportResultMeasureErrorModel(){
        setSuccessful(false);
        setCustomReportResponseType(CustomReportResponseType.MEASURE_ERROR);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}