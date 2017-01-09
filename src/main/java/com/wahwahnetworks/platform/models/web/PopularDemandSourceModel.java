package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.Map;

/**
 * Created by jhaygood on 3/4/16.
 */

@WebSafeModel
public class PopularDemandSourceModel {

    @JsonProperty("demand_source_name")
    private String demandSourceName;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("connection_type_key")
    private String connectionTypeKey;

    @JsonProperty("extra_data")
    private Map<String,String> extraData;

    public String getDemandSourceName() {
        return demandSourceName;
    }

    public void setDemandSourceName(String demandSourceName) {
        this.demandSourceName = demandSourceName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getConnectionTypeKey() {
        return connectionTypeKey;
    }

    public void setConnectionTypeKey(String connectionType) {
        this.connectionTypeKey = connectionType;
    }

    public Map<String, String> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }
}
