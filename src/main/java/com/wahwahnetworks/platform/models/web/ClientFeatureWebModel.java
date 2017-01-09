package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.ClientFeatureVariableType;

/**
 * Created by jhaygood on 11/13/15.
 */

@WebSafeModel
public class ClientFeatureWebModel {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("variable_name")
    private String variableName;

    @JsonProperty("variable_label")
    private String variableLabel;

    @JsonProperty("variable_type")
    private ClientFeatureVariableType variableType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public ClientFeatureVariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(ClientFeatureVariableType variableType) {
        this.variableType = variableType;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }

    @JsonProperty("display")
    public String getDisplayName(){
        return getName() + " - " + getDescription();
    }
}
