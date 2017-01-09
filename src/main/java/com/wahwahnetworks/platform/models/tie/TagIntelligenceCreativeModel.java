package com.wahwahnetworks.platform.models.tie;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.data.entities.enums.CreativePlatform;
import com.wahwahnetworks.platform.data.entities.enums.CreativeType;

/**
 * Created by jhaygood on 6/7/16.
 */
public class TagIntelligenceCreativeModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("creative_type")
    private CreativeType creativeType;

    @JsonProperty("tag_contents")
    private String tagContents;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("creative_platform")
    private CreativePlatform creativePlatform;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CreativeType getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(CreativeType creativeType) {
        this.creativeType = creativeType;
    }

    public String getTagContents() {
        return tagContents;
    }

    public void setTagContents(String tagContents) {
        this.tagContents = tagContents;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public CreativePlatform getCreativePlatform() {
        return creativePlatform;
    }

    public void setCreativePlatform(CreativePlatform creativePlatform) {
        this.creativePlatform = creativePlatform;
    }
}
