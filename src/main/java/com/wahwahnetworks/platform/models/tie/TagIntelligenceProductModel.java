package com.wahwahnetworks.platform.models.tie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jhaygood on 6/8/16.
 */
public class TagIntelligenceProductModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("line_items")
    private List<String> lineItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<String> lineItems) {
        this.lineItems = lineItems;
    }
}
