package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by jhaygood on 4/18/16.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "response_type", visible = true)
@JsonSubTypes
        ({
                @JsonSubTypes.Type(value = CustomReportResultCSVModel.class, name = "SUCCESS"),
                @JsonSubTypes.Type(value = CustomReportResultMeasureErrorModel.class, name = "MEASURE_ERROR")
        })
public class CustomReportResponseModel {

    @JsonProperty("is_successful")
    private boolean isSuccessful;

    @JsonProperty("response_type")
    private CustomReportResponseType customReportResponseType;

    @JsonProperty("request")
    private CustomReportRequestModel requestModel;


    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }


    public CustomReportRequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(CustomReportRequestModel requestModel) {
        this.requestModel = requestModel;
    }

    public CustomReportResponseType getCustomReportResponseType() {
        return customReportResponseType;
    }

    public void setCustomReportResponseType(CustomReportResponseType customReportResponseType) {
        this.customReportResponseType = customReportResponseType;
    }
}