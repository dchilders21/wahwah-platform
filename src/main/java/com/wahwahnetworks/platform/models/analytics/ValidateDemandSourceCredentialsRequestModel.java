package com.wahwahnetworks.platform.models.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by jhaygood on 3/21/16.
 */
public class ValidateDemandSourceCredentialsRequestModel {
    @JsonProperty("connection_details")
    private Map<String,String> connectionDetails;

    @JsonProperty("type_key")
    private String demandPartnerConnectionType;

    public Map<String, String> getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(Map<String, String> connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public String getDemandPartnerConnectionType() {
        return demandPartnerConnectionType;
    }

    public void setDemandPartnerConnectionType(String demandPartnerConnectionType) {
        this.demandPartnerConnectionType = demandPartnerConnectionType;
    }
}
