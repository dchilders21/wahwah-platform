package com.wahwahnetworks.platform.services.tie;

import com.newrelic.api.agent.NewRelic;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceAdModel;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceCreativeModel;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceLineItemModel;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceProductModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jndi.JndiTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.NamingException;
import java.util.concurrent.Future;

/**
 * Created by jhaygood on 6/8/16.
 */

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagIntelligenceService {

    private static final Logger log = Logger.getLogger(TagIntelligenceService.class);

    private final String TIE_BASE_URL;

    @Autowired
    public TagIntelligenceService(Environment environment){

        JndiTemplate jndiTemplate = new JndiTemplate();

        String baseUrl = "http://localhost:8080/tagintelligence";

        try {
            baseUrl = (String) jndiTemplate.lookup("java:comp/env/RedPandaTagIntelligenceURL");
        } catch (NamingException exception){
            if (environment == Environment.PRODUCTION)
            {
                log.error(exception);
                NewRelic.noticeError(exception);
            }
            else
            {
                log.info("RedPandaRabbitHost not set: Using default.");
            }
        }

        TIE_BASE_URL = baseUrl;

        log.info("Tag Intelligence URL: " + TIE_BASE_URL);
    }

    @Async
    public Future<TagIntelligenceLineItemModel> saveLineItem(TagIntelligenceLineItemModel lineItemModel){

        RestTemplate restTemplate = new RestTemplate();

        if(lineItemModel.getId() == null){
            lineItemModel = restTemplate.postForObject(TIE_BASE_URL + "/api/1.0/line-items/",lineItemModel,TagIntelligenceLineItemModel.class);
        } else {
            restTemplate.put(TIE_BASE_URL + "/api/1.0/line-items/{lineItemId}",lineItemModel,lineItemModel.getId());
        }

        return new AsyncResult<>(lineItemModel);
    }

    @Async
    public Future<TagIntelligenceAdModel> saveAd(TagIntelligenceAdModel adModel){

        RestTemplate restTemplate = new RestTemplate();

        if(adModel.getId() == null){
            adModel = restTemplate.postForObject(TIE_BASE_URL + "/api/1.0/ads/",adModel,TagIntelligenceAdModel.class);
        } else {
            restTemplate.put(TIE_BASE_URL + "/api/1.0/ads/{adId}",adModel,adModel.getId());
        }

        return new AsyncResult<>(adModel);
    }

    @Async
    public Future<TagIntelligenceCreativeModel> saveCreative(TagIntelligenceCreativeModel creativeModel){

        RestTemplate restTemplate = new RestTemplate();

        if(creativeModel.getId() == null){
            creativeModel = restTemplate.postForObject(TIE_BASE_URL + "/api/1.0/creatives/",creativeModel,TagIntelligenceCreativeModel.class);
        } else {
            restTemplate.put(TIE_BASE_URL + "/api/1.0/creatives/{creativeId}",creativeModel,creativeModel.getId());
        }

        return new AsyncResult<>(creativeModel);
    }

    @Async
    public Future<TagIntelligenceProductModel> saveProduct(TagIntelligenceProductModel productModel){

        log.info("Publishing Widget ID: " + productModel.getId());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(TIE_BASE_URL + "/api/1.0/products/{productId}",productModel,productModel.getId());

        return new AsyncResult<>(productModel);

    }
}
