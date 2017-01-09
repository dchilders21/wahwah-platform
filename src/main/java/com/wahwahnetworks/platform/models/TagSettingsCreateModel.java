package com.wahwahnetworks.platform.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

/**
 * Created by jhaygood on 5/18/16.
 */
public class TagSettingsCreateModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("format")
    private ProductFormat productFormat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductFormat getProductFormat() {
        return productFormat;
    }

    public void setProductFormat(ProductFormat productFormat) {
        this.productFormat = productFormat;
    }
}
