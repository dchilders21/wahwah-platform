package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by jhaygood on 6/29/16.
 */

@WebSafeModel
public class TargetableEntityModelList {

    @JsonProperty("targetable_entities")
    private List<TargetableEntityModel> targetableEntityModels;

    public TargetableEntityModelList(List<TargetableEntityModel> entityModelList){
        targetableEntityModels = entityModelList;
    }

    public List<TargetableEntityModel> getTargetableEntityModels() {
        return targetableEntityModels;
    }

    public void setTargetableEntityModels(List<TargetableEntityModel> targetableEntityModels) {
        this.targetableEntityModels = targetableEntityModels;
    }
}
