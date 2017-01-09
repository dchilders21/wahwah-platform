package com.wahwahnetworks.platform.models.rabbitmq;

/**
 * Created by jhaygood on 5/18/16.
 */
public class ProductRMQModel extends RMQDelayBase {
    private int productId;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
