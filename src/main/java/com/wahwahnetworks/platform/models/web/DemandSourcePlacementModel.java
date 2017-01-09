package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.DemandSourcePlacement;
import com.wahwahnetworks.platform.data.entities.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 7/20/16.
 */

@WebSafeModel
public class DemandSourcePlacementModel {
    @JsonProperty("id")
    private int id;

    @JsonProperty("placement_name")
    private String placementName;

    @JsonProperty("targeted_sites")
    private List<Integer> targetedSiteIds;

    public DemandSourcePlacementModel(){
        targetedSiteIds = new ArrayList<>();
    }

    public DemandSourcePlacementModel(DemandSourcePlacement placement){
        setId(placement.getId());
        setPlacementName(placement.getPlacementName());

        targetedSiteIds = new ArrayList<>();

        for(Site site : placement.getSites()){
            targetedSiteIds.add(site.getId());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public List<Integer> getTargetedSiteIds() {
        return targetedSiteIds;
    }

    public void setTargetedSiteIds(List<Integer> targetedSiteIds) {
        this.targetedSiteIds = targetedSiteIds;
    }
}
