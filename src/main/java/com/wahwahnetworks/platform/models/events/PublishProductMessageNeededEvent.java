package com.wahwahnetworks.platform.models.events;

import com.wahwahnetworks.platform.models.rabbitmq.ProductRMQModel;

/**
 * Created by jhaygood on 5/18/16.
 */
public class PublishProductMessageNeededEvent extends MessageNeededEvent<ProductRMQModel> {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PublishProductMessageNeededEvent(ProductRMQModel source) {
        super(source);
    }
}
