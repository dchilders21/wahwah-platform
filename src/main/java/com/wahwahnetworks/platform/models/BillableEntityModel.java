package com.wahwahnetworks.platform.models;

import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;

/**
 * Created by jhaygood on 3/31/16.
 */
public interface BillableEntityModel {
    BillableEntityType getBillableEntityTargetType();
    Integer getBillableEntityTargetId();
}
