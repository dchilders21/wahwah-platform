package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jhaygood on 2/19/16.
 */
public class TagSettingsProductModel {

    @JsonProperty("product")
    private ProductModel productModel;

    @JsonProperty("ad_units")
    private List<AdUnitModel> adUnits;

    @JsonProperty("line_items_targeted")
    private List<LineItemModel> lineItemsTargeted;

    public ProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    public List<AdUnitModel> getAdUnits() {
        return adUnits;
    }

    public void setAdUnits(List<AdUnitModel> adUnits) {
        this.adUnits = adUnits;
    }

    public List<LineItemModel> getLineItemsTargeted()
    {
        return lineItemsTargeted;
    }

    public void setLineItemsTargeted(List<LineItemModel> lineItemsTargeted)
    {
        this.lineItemsTargeted = lineItemsTargeted;
    }
}
