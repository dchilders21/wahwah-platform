package com.wahwahnetworks.platform.models.events;

import com.wahwahnetworks.platform.models.rabbitmq.RMQModel;
import org.springframework.context.ApplicationEvent;

/**
 * Created by jhaygood on 4/28/16.
 */
public abstract class MessageNeededEvent<T extends RMQModel> extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MessageNeededEvent(T source) {
        super(source);
    }

    public T getMessage(){
        return (T)super.getSource();
    }
}
