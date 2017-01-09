package com.wahwahnetworks.platform.services.tie;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.repos.LineItemRepository;
import com.wahwahnetworks.platform.data.repos.LineItemTargetingRepository;
import com.wahwahnetworks.platform.data.repos.ProductRepository;
import com.wahwahnetworks.platform.data.repos.SiteRepository;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceProductModel;
import com.wahwahnetworks.platform.services.TargetingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhaygood on 6/8/16.
 */

@Service
public class TagIntelligenceNetworkExportService {

    private static final Logger log = Logger.getLogger(TagIntelligenceNetworkExportService.class);

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private TagIntelligenceService tagIntelligenceService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagIntelligenceLineItemExportService lineItemExportService;

    @Transactional
    public void exportWidgetsForLineItem(LineItem lineItem, List<Integer> existingWidgets, List<LineItem> existingLineItems) throws Exception {

        Map<Integer,List<LineItem>> widgetLineItemMap = new HashMap<>();

        log.info("Preparing to export: " + lineItem.getName() + " on " + lineItem.getDemandSource().getName());

        if(lineItem.isArchived()){
            return;
        }

        boolean hasRunningAds = false;

        for(LineItemAd ad : lineItem.getAds()){
            if(!ad.isArchived()){
                hasRunningAds = true;
            }
        }

        if(!hasRunningAds){
            return;
        }

        List<Integer> widgets = targetingService.getWidgetsForLineItem(lineItem);

        log.info("Creating Widget To Line Item Map for Widgets");

        for(Integer widgetId : widgets){

            if(!widgetLineItemMap.containsKey(widgetId)){
                widgetLineItemMap.put(widgetId,new ArrayList<>());
            }

            if(!widgetLineItemMap.get(widgetId).contains(lineItem)){
                widgetLineItemMap.get(widgetId).add(lineItem);
            }
        }

        log.info("Exporting data for Widgets (Total: " + widgetLineItemMap.size() + ")");


        for(Integer widgetId : widgetLineItemMap.keySet()){

            if(widgetId == null || widgetId.equals(0)){
                continue;
            }

            if(existingWidgets.contains(widgetId)){
                continue;
            } else {
                existingWidgets.add(widgetId);
            }

            try {
                Product product = productRepository.findByWidgetId(widgetId);
                exportWidgetsForProduct(product, existingLineItems);
            } catch (IncorrectResultSizeDataAccessException exception){
                log.error(exception);
            }
        }

        log.info("Widgets Exported");
    }

    @Transactional
    public void exportWidgetsForProduct(Product product, List<LineItem> existingLineItems) throws Exception {

        if(product.getWidgetId() == null){
            return;
        }

        log.info("Exporting Widget ID: " + product.getWidgetId());

        List<LineItem> lineItems = targetingService.getLineItemsForProduct(product);

        TagIntelligenceProductModel productModel = new TagIntelligenceProductModel();
        productModel.setId(product.getWidgetId().toString());

        List<String> lineItemIds = new ArrayList<>();

        for(LineItem lineItemToExport : lineItems){

            if(!existingLineItems.contains(lineItemToExport)){
                lineItemExportService.exportLineItem(lineItemToExport);
                existingLineItems.add(lineItemToExport);
            }

            if(lineItemToExport.getPrimaryAdServerType() == AdServerType.RED_PANDA_TIE){
                lineItemIds.add(lineItemToExport.getPrimaryAdServerId());
            } else {
                lineItemIds.add(lineItemToExport.getPrimaryAdServerId());
            }
        }

        productModel.setLineItems(lineItemIds);
        tagIntelligenceService.saveProduct(productModel);

    }
}
