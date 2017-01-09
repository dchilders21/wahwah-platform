package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by jhaygood on 2/1/16.
 */

@WebSafeModel
public class DemandSourceConnectionTypeListModel {

    @JsonProperty("connection_types")
    private List<DemandSourceConnectionTypeModel> demandSourceConnectionTypeModels;

    public DemandSourceConnectionTypeListModel(List<DemandSourceConnectionTypeModel> demandSourceConnectionTypeModels){
        this.demandSourceConnectionTypeModels = demandSourceConnectionTypeModels;
    }
}
