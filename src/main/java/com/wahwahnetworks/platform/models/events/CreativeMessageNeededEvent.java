package com.wahwahnetworks.platform.models.events;

import com.wahwahnetworks.platform.models.rabbitmq.CreativeRMQModel;

/**
 * Created by jhaygood on 4/28/16.
 */
public class CreativeMessageNeededEvent extends MessageNeededEvent<CreativeRMQModel> {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CreativeMessageNeededEvent(CreativeRMQModel source) {
        super(source);
    }
}
