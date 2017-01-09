package com.wahwahnetworks.platform.lib.rabbit;

import org.springframework.amqp.core.AbstractExchange;

import java.util.Map;

/**
 * Created by jhaygood on 4/12/16.
 */
public class DelayedExchange extends AbstractExchange {

    public DelayedExchange(String name) {
        super(name);
    }

    public DelayedExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public DelayedExchange(String name, boolean durable, boolean autoDelete, Map<String,Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public final String getType() {
        return "x-delayed-message";
    }

}
