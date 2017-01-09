package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleType;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingCardinality;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.models.web.ProductModel;
import com.wahwahnetworks.platform.models.web.TargetableEntityModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhaygood on 6/23/16.
 */

@Service
public class TargetingService {

    private static final Logger log = Logger.getLogger(TargetingService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineItemTargetingRepository targetingRepository;

    @Autowired
    private SiteRepository siteRepository;

    private DataSource getDataSource(){
        EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) entityManager.getEntityManagerFactory();
        return info.getDataSource();
    }

    @Transactional(readOnly = true)
    public List<Integer> getWidgetsForLineItem(LineItem lineItem)
    {
        try {
            log.info("Generating Widget List For Line Item: " + lineItem.getName() + " on " + lineItem.getDemandSource().getName());

            List<LineItemTargeting> targetingList = lineItem.getTargeting();
            AccountNetwork network = null;

            if (lineItem.getPublisher() != null && lineItem.getPublisher().getParentAccount() != null) {
                network = (AccountNetwork) lineItem.getPublisher().getParentAccount();
            }

            if (lineItem.getDemandSource() != null && lineItem.getDemandSource().getAccount() != null) {
                network = (AccountNetwork) lineItem.getDemandSource().getAccount();
            }

            List<Integer> widgetList = new ArrayList<>();


            JdbcTemplate db = new JdbcTemplate(getDataSource());

            boolean isExclusive = (lineItem.getTargetingCardinality() == LineItemTargetingCardinality.EXCLUSIVE);

            if (!(targetingList != null && targetingList.size() > 0) || isExclusive) {

                Integer networkId = null;

                if (network != null) {
                    networkId = network.getId();
                }

                List<Map<String, Object>> resultMapList = db.queryForList("SELECT p.widget_id FROM products p INNER JOIN sites s ON p.site_id = s.id INNER JOIN account_publishers ap ON ap.account_id = s.account_id INNER JOIN accounts a ON a.id = ap.account_id WHERE a.parent_account_id <=> ?", networkId);

                for (Map<String, Object> resultMap : resultMapList) {
                    Integer widgetId = (Integer) resultMap.get("widget_id");
                    if (widgetId != null && widgetId != 0)
                        widgetList.add(widgetId);
                }
            }

            if (targetingList != null && targetingList.size() > 0) {
                for (LineItemTargeting targeting : targetingList) {
                    // Note: Assumed that only one of product, site, publisher are set. If that changes, this needs to be re-written
                    // Note: Assumed that product, publisher, and site were pre-filtered on network when line item targeting was created
                    if (targeting.getType() == LineItemTargetingRuleType.PRODUCT) {
                        if (targeting.getProduct() != null) {
                            List<Map<String, Object>> resultMapList = db.queryForList("SELECT p.widget_id FROM products p WHERE p.id = ?", targeting.getProduct().getId());

                            for (Map<String, Object> resultMap : resultMapList) {
                                Integer widgetId = (Integer) resultMap.get("widget_id");
                                if (widgetId != null  && widgetId != 0)
                                {
                                    if (!isExclusive)
                                        widgetList.add(widgetId);
                                    else
                                        widgetList.remove(widgetId);
                                }
                            }
                        }
                    }

                    if (targeting.getType() == LineItemTargetingRuleType.SITE) {
                        if (targeting.getSite() != null) {
                            List<Map<String, Object>> resultMapList = db.queryForList("SELECT p.widget_id FROM products p INNER JOIN sites s ON s.id = p.site_id WHERE s.id = ?", targeting.getSite().getId());

                            for (Map<String, Object> resultMap : resultMapList) {
                                Integer widgetId = (Integer) resultMap.get("widget_id");
                                if (widgetId != null && widgetId != 0)
                                {
                                    if (!isExclusive)
                                        widgetList.add(widgetId);
                                    else
                                        widgetList.remove(widgetId);
                                }
                            }
                        }
                    }

                    if (targeting.getType() == LineItemTargetingRuleType.PUBLISHER) {
                        if (targeting.getPublisher() != null) {
                            List<Map<String, Object>> resultMapList = db.queryForList("SELECT p.widget_id FROM products p INNER JOIN sites s ON s.id = p.site_id INNER JOIN account_publishers ap ON ap.account_id = s.account_id INNER JOIN accounts a ON a.id = ap.account_id WHERE a.id = ?", targeting.getPublisher().getId());

                            for (Map<String, Object> resultMap : resultMapList) {
                                Integer widgetId = (Integer) resultMap.get("widget_id");
                                if (widgetId != null && widgetId != 0)
                                {
                                    if (!isExclusive)
                                        widgetList.add(widgetId);
                                    else
                                        widgetList.remove(widgetId);
                                }
                            }
                        }
                    }

                    if (targeting.getType() == LineItemTargetingRuleType.NETWORK) {
                        if (targeting.getNetwork() != null) {
                            List<Map<String, Object>> resultMapList = db.queryForList("SELECT p.widget_id FROM products p INNER JOIN sites ms ON ms.id = p.site_id INNER JOIN sites s ON s.marketplace_site_id = s.id INNER JOIN account_publishers ap ON ap.account_id = s.account_id INNER JOIN accounts apa ON apa.id = ap.account_id INNER JOIN account_networks an ON an.account_id = apa.parent_account_id INNER JOIN accounts ana ON ana.id = an.account_id WHERE ana.id = ?", targeting.getNetwork().getId());

                            for (Map<String, Object> resultMap : resultMapList) {
                                Integer widgetId = (Integer) resultMap.get("widget_id");
                                if (widgetId != null && widgetId != 0)
                                {
                                    if (!isExclusive)
                                        widgetList.add(widgetId);
                                    else
                                        widgetList.remove(widgetId);
                                }
                            }
                        }
                    }
                }
            }


            return widgetList;
        } catch (Exception e){
            log.error(e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<TargetableEntityModel> getTargetableEntities(Integer accountId){

        Account account = null;

        if(accountId != null){
            account = accountRepository.findOne(accountId);
        }



        JdbcTemplate db = new JdbcTemplate(getDataSource());
        List<Map<String,Object>> resultMapList = new ArrayList<>();

        if(account == null || account.getAccountType() == AccountType.NETWORK){
            resultMapList = db.queryForList("SELECT s.id AS site_id,s.site_name,apa.name AS publisher_name, apa.id AS publisher_id, ana.id AS own_network_id, npp.id AS network_id, npp.name AS network_name FROM sites s INNER JOIN account_publishers ap ON ap.account_id = s.account_id INNER JOIN accounts apa ON apa.id = ap.account_id LEFT OUTER JOIN sites ns ON ns.marketplace_site_id = s.id LEFT OUTER JOIN accounts np ON np.id = ns.account_id LEFT OUTER JOIN accounts npp ON npp.id = np.parent_account_id LEFT OUTER JOIN account_networks an ON an.account_id = apa.parent_account_id LEFT OUTER JOIN accounts ana ON ana.id = an.account_id WHERE s.is_default = FALSE AND ana.id <=> ? ORDER BY network_name,publisher_name,site_name",accountId);
        }

        if(account != null && account.getAccountType() == AccountType.REPORTING_PRO){
            resultMapList = db.queryForList("SELECT s.id AS site_id,s.site_name,apa.name AS publisher_name, apa.id AS publisher_id, ara.id AS own_network_id, npp.id AS network_id, npp.name AS network_name FROM sites s INNER JOIN account_publishers ap ON ap.account_id = s.account_id INNER JOIN accounts apa ON apa.id = ap.account_id LEFT OUTER JOIN sites ns ON ns.marketplace_site_id = s.id LEFT OUTER JOIN accounts np ON np.id = ns.account_id LEFT OUTER JOIN accounts npp ON npp.id = np.parent_account_id LEFT OUTER JOIN account_reporting_pro ar ON ar.account_id = apa.parent_account_id LEFT OUTER JOIN accounts ara ON ara.id = ar.account_id WHERE s.is_default = FALSE AND ara.id <=> ? ORDER BY network_name,publisher_name,site_name",accountId);
        }

        List<TargetableEntityModel> modelList = new ArrayList<>();

        Map<Integer,TargetableEntityModel> entityMap = new HashMap<>();


        for(Map<String,Object> resultMap : resultMapList){

            Integer networkId = (Integer)resultMap.get("network_id");
            String networkName = (String)resultMap.get("network_name");

            if(networkId == null){

                if(accountId == null){
                    networkName = "Red Panda Marketplace";
                } else {
                    networkName = account.getName();
                }
            }

            if(!entityMap.containsKey(networkId)){
                TargetableEntityModel networkModel = new TargetableEntityModel();
                networkModel.setType(LineItemTargetingRuleType.NETWORK);
                networkModel.setId(networkId);
                networkModel.setName(networkName);

                entityMap.put(networkId,networkModel);
            }

            Integer publisherId = (Integer)resultMap.get("publisher_id");
            String publisherName = (String)resultMap.get("publisher_name");

            TargetableEntityModel publisherModel = new TargetableEntityModel();
            publisherModel.setType(LineItemTargetingRuleType.PUBLISHER);
            publisherModel.setId(publisherId);
            publisherModel.setName(publisherName);

            if(!entityMap.get(networkId).getChildren().contains(publisherModel)){
                entityMap.get(networkId).getChildren().add(publisherModel);
            } else {
                int i = entityMap.get(networkId).getChildren().indexOf(publisherModel);
                publisherModel = entityMap.get(networkId).getChildren().get(i);
            }

            Integer siteId = (Integer)resultMap.get("site_id");
            String siteName = (String)resultMap.get("site_name");

            TargetableEntityModel siteModel = new TargetableEntityModel();
            siteModel.setType(LineItemTargetingRuleType.SITE);
            siteModel.setId(siteId);
            siteModel.setName(siteName);

            if(!publisherModel.getChildren().contains(siteModel)){
                publisherModel.getChildren().add(siteModel);
            }
        }

        modelList.addAll(entityMap.values());

        return modelList;
    }

    @Transactional(readOnly = true)
    public List<LineItem> getLineItemsForProductModel(ProductModel productModel)
    {
        return getLineItemsForProduct(productRepository.findById(productModel.getId()));
    }

    @Transactional(readOnly = true)
    public List<LineItem> getLineItemsForProduct(Product product)
    {
        List<LineItem> lineItems = new ArrayList<>();
        List<LineItem> matchingLineItems = new ArrayList<>();

        // Product
        List<LineItemTargeting> productList = targetingRepository.findByProduct(product);

        for(LineItemTargeting targeting : productList){
            if(!lineItems.contains(targeting.getLineItem())){
                matchingLineItems.add(targeting.getLineItem());
            }
        }

        // Site
        List<LineItemTargeting> siteList = targetingRepository.findBySite(product.getSite());

        for(LineItemTargeting targeting : siteList){
            if(!lineItems.contains(targeting.getLineItem())){
                matchingLineItems.add(targeting.getLineItem());
            }
        }

        // Publisher
        List<LineItemTargeting> publisherList = targetingRepository.findByPublisher(product.getSite().getPublisher());

        for(LineItemTargeting targeting : publisherList){
            if(!lineItems.contains(targeting.getLineItem())){
                matchingLineItems.add(targeting.getLineItem());
            }
        }

        // Network
        if(product.getSite().getPublisher().getParentAccount() == null){
            // Marketplace. See if we have a linked network

            Site networkSite = siteRepository.findByMarketplaceSite(product.getSite());

            if(networkSite != null){

                AccountNetwork network = (AccountNetwork)networkSite.getPublisher().getParentAccount();

                List<LineItemTargeting> networkList = targetingRepository.findByNetwork(network);

                for(LineItemTargeting targeting : networkList){
                    if(!lineItems.contains(targeting.getLineItem())){
                        matchingLineItems.add(targeting.getLineItem());
                    }
                }
            }
        }

        Account parentNetwork = product.getSite().getPublisher().getParentAccount();
        List<LineItem> lineItemsForNetwork = lineItemRepository.findByDemandSourceAccountAndDemandSourceIsNotNull(parentNetwork);


        // Iterate Line Items
        for(LineItem lineItemInNetwork : lineItemsForNetwork){

            boolean isExclusive = (lineItemInNetwork.getTargetingCardinality() == LineItemTargetingCardinality.EXCLUSIVE);

            if(isExclusive){
                if(!matchingLineItems.contains(lineItemInNetwork)){
                    lineItems.add(lineItemInNetwork);
                }
            } else {
                if (lineItemInNetwork.getTargeting() == null || lineItemInNetwork.getTargeting().size() == 0) {
                    lineItems.add(lineItemInNetwork);
                }

                if(matchingLineItems.contains(lineItemInNetwork)){
                    lineItems.add(lineItemInNetwork);
                }
            }
        }


        List<LineItem> pausedLineItems = new ArrayList<>();

        // Remove any paused line items
        for(LineItem lineItem : lineItems){
            if(lineItem.isArchived()){
                pausedLineItems.add(lineItem);
            }

            boolean allAdsPaused = true;

            for(LineItemAd ad : lineItem.getAds()){
                allAdsPaused = ad.isArchived() && allAdsPaused;
            }

            if(allAdsPaused){
                pausedLineItems.add(lineItem);
            }
        }

        lineItems.removeAll(pausedLineItems);

        return lineItems;
    }
}
