package com.wahwahnetworks.platform.models.rabbitmq;

/**
 * Created by jhaygood on 4/26/16.
 */
public class CreativeRMQModel extends RMQDelayBase {
    private Integer creativeId;

    public Integer getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }
}
