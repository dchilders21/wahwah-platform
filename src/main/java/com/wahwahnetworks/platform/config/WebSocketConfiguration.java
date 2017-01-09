package com.wahwahnetworks.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


/**
 * Created by Justin on 12/26/2014.
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer
{
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry)
	{
		stompEndpointRegistry.addEndpoint("/stomp/toolbar-upload");
		stompEndpointRegistry.addEndpoint("/stomp/bulkpublish");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry)
	{
		messageBrokerRegistry.setApplicationDestinationPrefixes("/app/");
		messageBrokerRegistry.enableSimpleBroker("/queue/", "/topic/");
	}
}