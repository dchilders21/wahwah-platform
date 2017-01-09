package com.wahwahnetworks.platform.models.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.wahwahnetworks.platform.annotations.WebSafeModel
import com.wahwahnetworks.platform.data.entities.enums.Environment

/**
 * Created by jhaygood on 5/25/16.
 */
@WebSafeModel
data class ProductWidgetModel (
    @JsonProperty("widget_id") var widgetId:Int,
    @JsonProperty("environment") var environment:Environment,
    @JsonProperty("product_id") var productId:Int
)