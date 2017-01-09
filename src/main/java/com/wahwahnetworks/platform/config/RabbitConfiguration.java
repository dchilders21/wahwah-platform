package com.wahwahnetworks.platform.config;

import com.newrelic.api.agent.NewRelic;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.lib.rabbit.DelayedExchange;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhaygood on 3/17/16.
 */

@Configuration
public class RabbitConfiguration {

    private static final Logger log = Logger.getLogger(RabbitConfiguration.class);


    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, @SuppressWarnings("unused") AmqpAdmin admin){

        log.info("amqpTemplate. Setting retry policy to NeverRetryPolicy");

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // NOTE: Backoff policy is not being used. See DelayedInterval instead and DelayedExchange
        RetryTemplate retryTemplate = new RetryTemplate();

        retryTemplate.setRetryPolicy(new NeverRetryPolicy());

        rabbitTemplate.setRetryTemplate(retryTemplate);

        return rabbitTemplate;
    }

    @Bean
    public ConnectionFactory cachingConnectionFactory()
    {
        JndiTemplate jndiTemplate = new JndiTemplate();

        String rabbitHost;
        String rabbitUser;
        String rabbitPassword;

        try {
            rabbitHost = (String) jndiTemplate.lookup("java:comp/env/RedPandaRabbitHost");
        } catch (NamingException exception){
            if (environment() == Environment.PRODUCTION)
            {
                log.error(exception);
                NewRelic.noticeError(exception);
            }
            else
            {
                log.info("RedPandaRabbitHost not set: Using default.");
            }
            rabbitHost = "localhost";
        }

        try {
            rabbitUser = (String) jndiTemplate.lookup("java:comp/env/RedPandaRabbitUser");
        } catch (NamingException exception){
            if (environment() == Environment.PRODUCTION)
            {
                log.error(exception);
                NewRelic.noticeError(exception);
            }
            else
            {
                log.info("RedPandaRabbitUser not set: Using default.");
            }
            rabbitUser = "guest";
        }

        try {
            rabbitPassword = (String) jndiTemplate.lookup("java:comp/env/RedPandaRabbitPassword");
        } catch (NamingException exception){
            if (environment() == Environment.PRODUCTION)
            {
                log.error(exception);
                NewRelic.noticeError(exception);
            }
            else
            {
                log.info("RedPandaRabbitPassword not set: Using default.");
            }
            rabbitPassword = "guest";
        }

        log.info("Rabbit Host Name: " + rabbitHost);
        log.info("Rabbit Username: " + rabbitUser);
        log.info("Rabbit Password: " + rabbitPassword);

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost);
        connectionFactory.setUsername(rabbitUser);
        connectionFactory.setPassword(rabbitPassword);
        return connectionFactory;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        /*
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        rabbitListenerContainerFactory.setConcurrentConsumers(1);
        rabbitListenerContainerFactory.setMaxConcurrentConsumers(1);
        rabbitListenerContainerFactory.setDefaultRequeueRejected(false);
        rabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return rabbitListenerContainerFactory;*/

        return exclusiveRabbitListenerContainerFactory(connectionFactory);
    }

    @Bean(name = "exclusiveRabbitListenerFactory")
    public RabbitListenerContainerFactory exclusiveRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        rabbitListenerContainerFactory.setConcurrentConsumers(1);
        rabbitListenerContainerFactory.setMaxConcurrentConsumers(1);
        rabbitListenerContainerFactory.setDefaultRequeueRejected(false);
        rabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return rabbitListenerContainerFactory;
    }

    // Only really things below here need to be touched without good reason.

    @Bean
    public AmqpAdmin ampqAdmin(ConnectionFactory connectionFactory){
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);

/***** Exchanges *****/
        admin.declareExchange(networkExchange());
        admin.declareExchange(publisherExchange());
        admin.declareExchange(siteExchange());
        admin.declareExchange(demandSourceExchange());
        admin.declareExchange(productExchange());
        admin.declareExchange(delayedExchange());

/***** Queues *****/
        admin.declareQueue(publishProductListener());

        // Tag Intelligence
        admin.declareQueue(updateTagIntelligenceCreativeListener());
        admin.declareQueue(updateTagIntelligenceLineItemListener());

/***** Bindings *****/

        admin.declareBinding(publishProductToPublishProductListener());
        admin.declareBinding(publishProductDelayedToPublishProductListener());

        // Tag Intelligence
        admin.declareBinding(updateCreativeToTagIntelligenceListener());
        admin.declareBinding(updateDelayedCreativeToTagIntelligenceListener());
        admin.declareBinding(updateLineItemToTagIntelligenceListener());
        admin.declareBinding(updateDelayedLineItemToTagIntelligenceListener());

        return admin;
    }

    /***** Exchange create methods *****/
    @Bean
    public Exchange networkExchange(){
        return new DirectExchange("Network",true,false);
    }

    @Bean
    public Exchange publisherExchange(){
        return new DirectExchange("Publisher",true,false);
    }

    @Bean
    public Exchange siteExchange(){
        return new DirectExchange("Site",true,false);
    }

    @Bean
    public Exchange demandSourceExchange(){
        return new DirectExchange("DemandSource",true,false);
    }

    public Exchange productExchange(){ return new DirectExchange("Product",true,false); }

    @Bean
    public Exchange delayedExchange(){

        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct");

        DelayedExchange delayedExchange = new DelayedExchange("Delayed",true,false,arguments);
        return delayedExchange;
    }

    /***** Listener create methods *****/

    @Bean
    public Queue updateTagIntelligenceCreativeListener(){
        return new Queue("UpdateTagIntelligenceCreativeListener");
    }

    @Bean
    public Queue updateTagIntelligenceLineItemListener(){
        return new Queue("UpdateTagIntelligenceLineItemListener");
    }

    public Queue publishProductListener(){ return new Queue("PublishProductListener"); }

    // Delayed Queue Routing Keys
    // Warning: Change sparingly b/c existing messages may still be getting processed
    public static final String TagIntelligenceCreativeSaveOrUpdateDelayRoutingKey = "TagIntelligenceCreativeSaveOrUpdate";
    public static final String TagIntelligenceLineItemSaveOrUpdateDelayRoutingKey = "TagIntelligenceLineItemSaveOrUpdate";
    public static final String ProductPublishDelayRoutingKey = "ProductPublish";

    /***** Binding create methods *****/

    @Bean
    public Binding updateCreativeToTagIntelligenceListener(){
        return BindingBuilder.bind(updateTagIntelligenceCreativeListener()).to(demandSourceExchange()).with("UpdateCreative").noargs();
    }

    @Bean
    public Binding updateLineItemToTagIntelligenceListener(){
        return BindingBuilder.bind(updateTagIntelligenceLineItemListener()).to(demandSourceExchange()).with("UpdateLineItem").noargs();
    }

    @Bean
    public Binding updateDelayedCreativeToTagIntelligenceListener(){
        return BindingBuilder.bind(updateTagIntelligenceCreativeListener()).to(delayedExchange()).with(TagIntelligenceCreativeSaveOrUpdateDelayRoutingKey).noargs();
    }

    @Bean
    public Binding updateDelayedLineItemToTagIntelligenceListener(){
        return BindingBuilder.bind(updateTagIntelligenceLineItemListener()).to(delayedExchange()).with(TagIntelligenceLineItemSaveOrUpdateDelayRoutingKey).noargs();
    }

    @Bean
    public Binding publishProductToPublishProductListener(){
        return BindingBuilder.bind(publishProductListener()).to(productExchange()).with("Publish").noargs();
    }

    @Bean
    public Binding publishProductDelayedToPublishProductListener(){
        return BindingBuilder.bind(publishProductListener()).to(delayedExchange()).with(ProductPublishDelayRoutingKey).noargs();
    }

    // From WahwahPlatformConfiguration

    @Bean(destroyMethod = "")
    public Environment environment()
    {
        try
        {
            JndiTemplate jndiTemplate = new JndiTemplate();
            Environment environment = Environment.valueOf((String) jndiTemplate.lookup("java:comp/env/environment_name"));
            return environment;
        }
        catch (Exception e)
        {
            return Environment.OTHER;
        }
    }


}
