package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 2/1/16.
 */
public class DemandSourceConnectionTypeModel {

    @JsonProperty("type_key")
    private String typeKey;

    @JsonProperty("name")
    private String name;

    @JsonProperty("connection_metadata_encoded")
    private String connectionMetaData;

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionMetaData() {
        return connectionMetaData;
    }

    public void setConnectionMetaData(String connectionMetaData) {
        this.connectionMetaData = connectionMetaData;
    }
}
