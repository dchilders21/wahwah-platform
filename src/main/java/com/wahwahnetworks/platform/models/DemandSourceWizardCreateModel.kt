package com.wahwahnetworks.platform.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.wahwahnetworks.platform.models.web.DemandSourceConnectionModel
import com.wahwahnetworks.platform.models.web.DemandSourceModel

/**
 * Created by jhaygood on 3/9/16.
 */
data class DemandSourceWizardCreateModel (
        @JsonProperty("demand_source") val demandSource: DemandSourceModel,
        @JsonProperty("connection") val connection: DemandSourceConnectionModel
)