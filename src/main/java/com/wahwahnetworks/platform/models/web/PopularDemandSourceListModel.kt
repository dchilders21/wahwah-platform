package com.wahwahnetworks.platform.models.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.wahwahnetworks.platform.annotations.WebSafeModel

/**
 * Created by jhaygood on 3/4/16.
 */

@WebSafeModel
class PopularDemandSourceListModel(
        @JsonProperty("demand_sources") val demandSources:List<PopularDemandSourceModel>
)