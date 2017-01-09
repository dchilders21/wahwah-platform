package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.*;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.lib.MoneyUtils;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.tie.TagIntelligenceLineItemExportService;
import com.wahwahnetworks.platform.services.tie.TagIntelligenceNetworkExportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jhaygood on 4/19/16.
 */

@Service
public class LineItemService {

    private static final Logger log = Logger.getLogger(LineItemService.class);

    @Autowired
    private DemandSourceRepository demandSourceRepository;

    @Autowired
    private CreativeRepository creativeRepository;

    @Autowired
    private DemandSourceService demandSourceService;

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineItemAdRepository lineItemAdRepository;

    @Autowired
    private AccountNetworkRepository networkRepository;

    @Autowired
    private LineItemTargetingRepository lineItemTargetingRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagIntelligenceLineItemExportService lineItemExportService;

    @Autowired
    private AccountPublisherRepository publisherRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private TagIntelligenceNetworkExportService networkExportService;

    @Autowired
    private TargetingService targetingService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<LineItemModel> getLineItemsForDemandSourceId(int demandSourceId){

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);

        List<LineItemModel> lineItemModels = new ArrayList<>();

        List<LineItem> lineItems = lineItemRepository.findByDemandSource(demandSource);

        for(LineItem lineItem : lineItems)
        {
            LineItemModel lineItemModel = new LineItemModel(lineItem);

            // Output warning if no creative for an ad (otherwise this functionality does nothing)
            for(LineItemAd lineItemAd : lineItem.getAds())
            {
                if (lineItemAd.getCreative() == null)
                    log.warn("No creative for lineItemAd " + lineItemAd.getId() + " of line item "+lineItemAd.getLineItem().getId()+"! This shouldn't happen.");
            }

            lineItemModels.add(lineItemModel);
        }

        return lineItemModels;
    }

    @Transactional
    public void deleteLineItem(LineItem lineItem)
    {
        Collection<LineItemTargeting> oldTargeting = lineItemTargetingRepository.findByLineItem(lineItem);
        for(LineItemTargeting oldTarget : oldTargeting)
        {
            lineItemTargetingRepository.delete(oldTarget);
        }
        Iterable<LineItemAd> lineItemAds = lineItemAdRepository.findAllByLineItemId(lineItem.getId());
        for (LineItemAd lia: lineItemAds)
        {
            lineItemAdRepository.delete(lia.getId());
        }
        lineItemRepository.delete(lineItem);
    }

    @Transactional
    public void saveLineItems(DemandSourceModel demandSourceModel, List<LineItemModel> lineItemModels) throws Exception {

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceModel.getDemandSourceId());

        log.info("Saving Line Items For Demand Source: " + demandSource.getName());

        log.info("Deleting old line items");

        List<Integer> widgetsToPublish = new ArrayList<>();

        // Delete old line items no longer present
        for (LineItem oldLineItem : lineItemRepository.findByDemandSource(demandSource))
        {
            Boolean stillExists = false;

            for(LineItemModel lineItemModel : lineItemModels){
                if (lineItemModel.getId() != null && lineItemModel.getId().equals(new Integer(oldLineItem.getId())))
                {
                    stillExists = true;
                }
            }
            if (!stillExists)
            {
                widgetsToPublish.addAll(targetingService.getWidgetsForLineItem(oldLineItem));
                deleteLineItem(oldLineItem);
            }
        }

        log.info("Flushing data to database to ensure consistent views");

        entityManager.flush();

        List<LineItem> lineItemsToExport = new ArrayList<>();

        log.info("Saving Line Items");

        for(LineItemModel lineItemModel : lineItemModels){

            log.info("Saving Line Item: " + lineItemModel.getName());

            LineItem lineItem = null;
            boolean isCreating = true;

            if(lineItemModel.getId() != null){
                lineItem = lineItemRepository.findOne(lineItemModel.getId());
                isCreating = false;

                widgetsToPublish.addAll(targetingService.getWidgetsForLineItem(lineItem));
            }

            if(lineItem == null){
                isCreating = true;
                lineItem = new LineItem();
                lineItem.setDemandSource(demandSource);
            }

            lineItem.setName(lineItemModel.getName());
            lineItem.setArchived(lineItemModel.isPaused());

            if(lineItemModel.isEnableManualWeighting()){
                lineItem.setAdWeightingMode(LineItemAdWeightingMode.MANUAL);
            } else {
                lineItem.setAdWeightingMode(LineItemAdWeightingMode.EQUAL);
            }

            if(isCreating || lineItem.getDeliveryMode() == null){ // Only allow delivery mode to be set during creation
                lineItem.setDeliveryMode(lineItemModel.getDeliveryMode());
            }

            lineItem.setSupportsMobile(false);

            if(lineItemModel.getSupportsMobile() != null){
                lineItem.setSupportsMobile(lineItemModel.getSupportsMobile());
            }

            lineItem.setCpmType(lineItemModel.getCpmType());

            lineItem.setExpectedCPMInCents(MoneyUtils.getCentsForDollarString(lineItemModel.getCpm()));

            lineItem.setTargetingCardinality(lineItemModel.getTargetingCardinality());

            lineItemRepository.save(lineItem);

            Collection<LineItemTargeting> oldTargeting = lineItemTargetingRepository.findByLineItem(lineItem);

            for(LineItemTargeting oldTarget : oldTargeting){
                if(lineItemModel.getTargeting().stream().filter(t -> t.getId() != null && t.getId() == oldTarget.getId()).count() == 0){
                    lineItem.getTargeting().remove(oldTarget); // Important to prevent reference errors later PLATFORM-1064
                    lineItemTargetingRepository.delete(oldTarget);
                }
            }

            lineItemRepository.save(lineItem);

            for (LineItemTargetingModel targetingModel: lineItemModel.getTargeting())
            {
                createOrUpdateTargeting( lineItem, targetingModel); // ! Also saves LineItemTargetingModel
            }

            for(DemandSourceLineItemAdModel adModel : lineItemModel.getAds()){

                LineItemAd lineItemAd = null;

                if(adModel.getId() != null){
                    lineItemAd = lineItemAdRepository.findOne(adModel.getId());
                }

                if(lineItemAd == null){
                    lineItemAd = new LineItemAd();
                    lineItemAd.setLineItem(lineItem);
                    lineItem.getAds().add(lineItemAd);
                }

                lineItemAd.setArchived(adModel.isPaused());
                lineItemAd.setWeightPercentage(adModel.getWeightPercentage());
                lineItemAd.setName(adModel.getName());

                Creative creative = creativeRepository.findOne(adModel.getCreativeId());
                lineItemAd.setCreative(creative);

                lineItemAdRepository.save(lineItemAd);
            }

            lineItemExportService.exportLineItem(lineItem);

            lineItemsToExport.add(lineItem);
        }

        entityManager.flush();

        List<Integer> widgetsPublished = new ArrayList<>();
        List<LineItem> lineItemsPublished = new ArrayList<>();

        for(Integer widgetId : widgetsToPublish){
            if (widgetId != null && widgetId != 0 && !widgetsPublished.contains(widgetId)) {
                Product product = productRepository.findByWidgetId(widgetId);
                networkExportService.exportWidgetsForProduct(product,lineItemsPublished);
                widgetsPublished.add(widgetId);
            }
            else if (widgetId == null)
            {
                log.warn("widgetId is NULL. Skipping");
            }
        }
    }

    public void createOrUpdateTargeting(LineItem lineItem, LineItemTargetingModel targetingModel)
    {
        targetingModel.setLineItemId(lineItem.getId());
        if (targetingModel.getId() == null || null == lineItemTargetingRepository.findOne(targetingModel.getId()))
        {
            LineItemTargeting newTargeting = lineItemTargetingEntityFromModel(lineItem,targetingModel);
            lineItemTargetingRepository.save(newTargeting);
        }
        else
        {
            LineItemTargeting oldTargeting = lineItemTargetingRepository.findOne(targetingModel.getId());
            LineItemTargeting newTargeting = lineItemTargetingEntityFromModel(lineItem,targetingModel);
            newTargeting.setLineItem(oldTargeting.getLineItem()); // Can't change line item
            lineItemTargetingRepository.save(newTargeting);
        }
    }

    public LineItemTargetingModel lineItemTargetingModelFromEntity(LineItemTargeting targeting)
    {
        LineItemTargetingModel targetingModel = new LineItemTargetingModel();
        targetingModel.setId(targeting.getId());
        targetingModel.setName(targeting.getName());
        targetingModel.setType(targeting.getType());

        if (targeting.getLineItem() != null){
            targetingModel.setLineItemId(targeting.getLineItem().getId());
        }

        if (targeting.getPublisher() != null){
            targetingModel.setPublisherId(targeting.getPublisher().getId());
        }

        if (targeting.getProduct() != null){
            targetingModel.setProductId(targeting.getProduct().getId());
        }

        if (targeting.getSite() != null){
            targetingModel.setSiteId(targeting.getSite().getId());
        }

        if (targeting.getDisplayType() != null){
            targetingModel.setDisplayType(targeting.getDisplayType());
        } else {
            targetingModel.setDisplayType(LineItemTargetingRuleDisplayType.BACKUP);
        }


        return targetingModel;
    }


    public LineItemTargeting lineItemTargetingEntityFromModel(LineItem lineItem, LineItemTargetingModel lineItemTargetingModel)
    {
        LineItemTargeting targeting = new LineItemTargeting();

        if (lineItemTargetingModel.getId() != null){
            targeting.setId(lineItemTargetingModel.getId());
        }

        targeting.setLineItem(lineItem);

        targeting.setName(lineItemTargetingModel.getName());
        targeting.setType(lineItemTargetingModel.getType());

        targeting.setPublisher(null);
        targeting.setProduct(null);
        targeting.setSite(null);
        targeting.setNetwork(null);

        if (lineItemTargetingModel.getPublisherId() != null){
            targeting.setPublisher(publisherRepository.findOne(lineItemTargetingModel.getPublisherId()));
        }

        if (lineItemTargetingModel.getProductId() != null){
            targeting.setProduct(productRepository.findOne(lineItemTargetingModel.getProductId()));
        }

        if (lineItemTargetingModel.getSiteId() != null){
            targeting.setSite(siteRepository.findOne(lineItemTargetingModel.getSiteId()));
        }

        if (lineItemTargetingModel.getNetworkId() != null){
            targeting.setNetwork(networkRepository.findOne(lineItemTargetingModel.getNetworkId()));
        }

        targeting.setDisplayType(lineItemTargetingModel.getDisplayType());
        return targeting;
    }

    public Collection<LineItemTargeting> getTargetingRuleModelsForLineItem(Integer lineItemId)
    {
        return lineItemTargetingRepository.findByLineItem(lineItemRepository.getOne(lineItemId));
    }

    @Transactional
    public void deleteTargetingForPublisher(AccountPublisher publisher){
        Iterable<LineItemTargeting> targetingList = lineItemTargetingRepository.findByPublisher(publisher);

        for(LineItemTargeting targeting : targetingList){
            lineItemTargetingRepository.delete(targeting);
        }
    }

    @Transactional
    public void deleteTargetingForSite(Site site){
        Iterable<LineItemTargeting> targetingList = lineItemTargetingRepository.findBySite(site);

        for(LineItemTargeting targeting : targetingList){
            lineItemTargetingRepository.delete(targeting);
        }
    }

    @Transactional
    public LineItem ensureMarketplaceLineItemForSite(Site networkSite){

        if(networkSite.getPublisher().getParentAccount() instanceof AccountNetwork) {

            AccountNetwork network = (AccountNetwork) networkSite.getPublisher().getParentAccount();

            if (network.getMarketplaceDemandSource() == null) {
                DemandSourceModel demandSourceModel = new DemandSourceModel();
                demandSourceModel.setName(network.getName() + " - Red Panda Marketplace");

                DemandSource demandSource = demandSourceRepository.findByName(demandSourceModel.getName());

                if (demandSource == null) {
                    demandSourceModel = demandSourceService.createDemandSourceFromModel(demandSourceModel, network.getId());
                    demandSource = demandSourceRepository.findOne(demandSourceModel.getDemandSourceId());
                }

                network.setMarketplaceDemandSource(demandSource);
                networkRepository.save(network);
            }

            Site marketplaceSite = networkSite.getMarketplaceSite();
            Product marketplaceProduct = marketplaceSite.getDefaultProduct();
            ProductTagListEntry tagEntry = productService.grabTag(marketplaceProduct);


            if (networkSite.getMarketplaceCreative() == null) {

                Creative creative = new Creative();
                creative.setDemandSource(network.getMarketplaceDemandSource());
                creative.setVendorType("RED_PANDA_MARKETPLACE");
                creative.setBiddable(false);
                creative.setCreativeType(CreativeType.HTML);
                creative.setName("Red Panda Marketplace - " + networkSite.getSiteName() + " - " + marketplaceProduct.getName());
                creative.setTagContents(tagEntry.getTag());
                creative.setCreativePlatform(CreativePlatform.ALL);

                creativeRepository.save(creative);
                networkSite.setMarketplaceCreative(creative);

                siteRepository.save(networkSite);
            } else {
                networkSite.getMarketplaceCreative().setName("Red Panda Marketplace - " + networkSite.getSiteName() + " - " + marketplaceProduct.getName());
                networkSite.getMarketplaceCreative().setTagContents(tagEntry.getTag());
                creativeRepository.save(networkSite.getMarketplaceCreative());
            }

            if (networkSite.getMarketplaceLineItem() == null) {
                LineItem lineItem = new LineItem();
                lineItem.setDemandSource(network.getMarketplaceDemandSource());
                lineItem.setDeliveryMode(LineItemDeliveryMode.WEB);
                lineItem.setAdWeightingMode(LineItemAdWeightingMode.EQUAL);
                lineItem.setCpmType(LineItemCPMType.ESTIMATED);
                lineItem.setExpectedCPMInCents(0);
                lineItem.setName("Red Panda Marketplace - " + networkSite.getSiteName());
                lineItem.setSupportsMobile(true);
                networkSite.setMarketplaceLineItem(lineItem);
                lineItem = lineItemRepository.save(lineItem);

                List<LineItemTargeting> targetingList = new ArrayList<>();
                LineItemTargeting targeting = new LineItemTargeting();
                targeting.setLineItem(lineItem);
                targeting.setName(networkSite.getSiteName());
                targeting.setSite(networkSite);
                targeting.setType(LineItemTargetingRuleType.SITE);
                targeting.setDisplayType(LineItemTargetingRuleDisplayType.FIRST_LOOK);

                targetingList.add(targeting);
                lineItem.setTargeting(targetingList);

                lineItemTargetingRepository.save(targeting);
                lineItem = lineItemRepository.save(lineItem);

                List<LineItemAd> adList = new ArrayList<>();

                LineItemAd ad = new LineItemAd();
                ad.setName("Red Panda Marketplace - " + networkSite.getSiteName());
                ad.setLineItem(lineItem);
                ad.setCreative(networkSite.getMarketplaceCreative());

                adList.add(ad);
                lineItem.setAds(adList);

                lineItemAdRepository.save(ad);
                lineItemRepository.save(lineItem);

                siteRepository.save(networkSite);

            }

            return networkSite.getMarketplaceLineItem();
        }

        throw new IllegalArgumentException("Argument 'networkSite' needs to be a Site owned by an AccountNetwork");
    }
}
