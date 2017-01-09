package com.wahwahnetworks.platform.models.events;

import com.wahwahnetworks.platform.models.rabbitmq.LineItemRMQModel;

/**
 * Created by jhaygood on 4/28/16.
 */
public class LineItemMessageNeededEvent extends MessageNeededEvent<LineItemRMQModel> {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public LineItemMessageNeededEvent(LineItemRMQModel source) {
        super(source);
    }
}
