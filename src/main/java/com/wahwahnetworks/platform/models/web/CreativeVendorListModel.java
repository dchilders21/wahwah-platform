package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 6/20/16.
 */

@WebSafeModel
public class CreativeVendorListModel {
    @JsonProperty("vendors")
    private List<CreativeVendorModel> creativeVendors;

    public CreativeVendorListModel(){
        creativeVendors = new ArrayList<>();
    }

    public List<CreativeVendorModel> getCreativeVendors() {
        return creativeVendors;
    }

    public void setCreativeVendors(List<CreativeVendorModel> creativeVendors) {
        this.creativeVendors = creativeVendors;
    }
}
