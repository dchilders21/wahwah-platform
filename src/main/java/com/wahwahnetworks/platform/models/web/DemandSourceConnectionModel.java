package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.DemandSourceConnection;
import com.wahwahnetworks.platform.lib.JsonSerializer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jhaygood on 1/28/16.
 */

@WebSafeModel
public class DemandSourceConnectionModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("demand_source_id")
    private int demandSourceId;

    @JsonProperty("type_key")
    private String connectionTypeKey;

    @JsonProperty("type_name")
    private String connectionTypeName;

    @JsonProperty("connection_details")
    private Map<String,String> connectionDetailMap;

    public DemandSourceConnectionModel(){

    }

    public DemandSourceConnectionModel(DemandSourceConnection demandSourceConnection) throws IOException {
        setId(demandSourceConnection.getId());
        setDemandSourceId(demandSourceConnection.getDemandSource().getId());
        setConnectionTypeKey(demandSourceConnection.getDemandSourceConnectionType().getTypeKey());
        setConnectionTypeName(demandSourceConnection.getDemandSourceConnectionType().getName());

        Map<String, String> connectionDetails = (Map<String, String>) JsonSerializer.deserialize(demandSourceConnection.getConnectionDetails(), Map.class);
        setConnectionDetailMap(connectionDetails);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConnectionTypeKey() {
        return connectionTypeKey;
    }

    public void setConnectionTypeKey(String connectionTypeKey) {
        this.connectionTypeKey = connectionTypeKey;
    }

    public String getConnectionTypeName() {
        return connectionTypeName;
    }

    public void setConnectionTypeName(String connectionTypeName) {
        this.connectionTypeName = connectionTypeName;
    }

    public int getDemandSourceId() {
        return demandSourceId;
    }

    public void setDemandSourceId(int demandSourceId) {
        this.demandSourceId = demandSourceId;
    }

    public Map<String, String> getConnectionDetailMap() {
        return connectionDetailMap;
    }

    public void setConnectionDetailMap(Map<String, String> connectionDetailMap) {
        this.connectionDetailMap = connectionDetailMap;
    }
}
