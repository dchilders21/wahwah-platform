package com.wahwahnetworks.platform.models.rabbitmq;

/**
 * Created by jhaygood on 4/27/16.
 */
public class LineItemRMQModel extends RMQDelayBase {
    private int lineItemId;

    public int getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(int lineItemId) {
        this.lineItemId = lineItemId;
    }
}
