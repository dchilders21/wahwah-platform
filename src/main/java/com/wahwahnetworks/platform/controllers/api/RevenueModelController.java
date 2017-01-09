package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.data.entities.BillableEntity;
import com.wahwahnetworks.platform.data.entities.RevenueModel;
import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;
import com.wahwahnetworks.platform.models.BillableEntityModel;
import com.wahwahnetworks.platform.models.web.RevenueWebModel;
import com.wahwahnetworks.platform.models.web.RevenueWebModelList;
import com.wahwahnetworks.platform.services.RevenueModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 4/7/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/revenue-model")
public class RevenueModelController extends BaseAPIController {

    @Autowired
    private RevenueModelService revenueModelService;

    @RequestMapping(value = "network/{networkId}", method = RequestMethod.GET)
    @Transactional
    public RevenueWebModelList getRevenueModelForNetwork(@PathVariable int networkId){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.NETWORK;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return networkId;
            }
        });

        Iterable<RevenueModel> revenueModels = revenueModelService.getRevenueModelsForBillableEntity(billableEntity);
        RevenueWebModelList revenueWebModelList = new RevenueWebModelList(revenueModels);
        return revenueWebModelList;
    }

    @RequestMapping(value = "network/{networkId}", method = RequestMethod.PUT)
    @Transactional
    public RevenueWebModelList putRevenueModelForNetwork(@PathVariable int networkId, @RequestBody List<RevenueWebModel> models){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.NETWORK;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return networkId;
            }
        });

        for(RevenueWebModel model : models){
            revenueModelService.updateRevenueModel(billableEntity,model);
        }

        return getRevenueModelForNetwork(networkId);
    }

    @RequestMapping(value = "publisher/{publisherId}", method = RequestMethod.GET)
    @Transactional
    public RevenueWebModelList getRevenueModelForPublisher(@PathVariable int publisherId){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.PUBLISHER;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return publisherId;
            }
        });

        Iterable<RevenueModel> revenueModels = revenueModelService.getRevenueModelsForBillableEntity(billableEntity);
        RevenueWebModelList revenueWebModelList = new RevenueWebModelList(revenueModels);
        return revenueWebModelList;
    }

    @RequestMapping(value = "publisher/{publisherId}", method = RequestMethod.PUT)
    @Transactional
    public RevenueWebModelList putRevenueModelForPublisher(@PathVariable int publisherId, @RequestBody List<RevenueWebModel> models){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.PUBLISHER;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return publisherId;
            }
        });

        for(RevenueWebModel model : models){
            revenueModelService.updateRevenueModel(billableEntity,model);
        }

        return getRevenueModelForPublisher(publisherId);
    }

    @RequestMapping(value = "site/{siteId}", method = RequestMethod.GET)
    @Transactional
    public RevenueWebModelList getRevenueModelForSite(@PathVariable int siteId){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.SITE;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return siteId;
            }
        });

        Iterable<RevenueModel> revenueModels = revenueModelService.getRevenueModelsForBillableEntity(billableEntity);
        RevenueWebModelList revenueWebModelList = new RevenueWebModelList(revenueModels);
        return revenueWebModelList;
    }

    @RequestMapping(value = "site/{siteId}", method = RequestMethod.PUT)
    @Transactional
    public RevenueWebModelList putRevenueModelForSite(@PathVariable int siteId, @RequestBody List<RevenueWebModel> models){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.SITE;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return siteId;
            }
        });

        for(RevenueWebModel model : models){
            revenueModelService.updateRevenueModel(billableEntity,model);
        }

        return getRevenueModelForSite(siteId);
    }


    @RequestMapping(value = "product/{productId}", method = RequestMethod.GET)
    @Transactional
    public RevenueWebModelList getRevenueModelForProduct(@PathVariable int productId){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.PRODUCT;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return productId;
            }
        });

        Iterable<RevenueModel> revenueModels = revenueModelService.getRevenueModelsForBillableEntity(billableEntity);
        RevenueWebModelList revenueWebModelList = new RevenueWebModelList(revenueModels);
        return revenueWebModelList;
    }

    @RequestMapping(value = "demand-source-placement/{placementId}", method = RequestMethod.GET)
    @Transactional
    public RevenueWebModelList getRevenueModelForPlacement(@PathVariable int placementId){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.DEMAND_SOURCE_PLACEMENT;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return placementId;
            }
        });

        Iterable<RevenueModel> revenueModels = revenueModelService.getRevenueModelsForBillableEntity(billableEntity);
        RevenueWebModelList revenueWebModelList = new RevenueWebModelList(revenueModels);
        return revenueWebModelList;
    }

    @RequestMapping(value = "demand-source-placement/{placementId}", method = RequestMethod.PUT)
    @Transactional
    public RevenueWebModelList putRevenueModelForPlacement(@PathVariable int placementId, @RequestBody List<RevenueWebModel> models){
        BillableEntity billableEntity = revenueModelService.getOrCreateBillableEntity(new BillableEntityModel() {
            @Override
            public BillableEntityType getBillableEntityTargetType() {
                return BillableEntityType.DEMAND_SOURCE_PLACEMENT;
            }

            @Override
            public Integer getBillableEntityTargetId() {
                return placementId;
            }
        });

        for(RevenueWebModel model : models){
            revenueModelService.updateRevenueModel(billableEntity,model);
        }

        return getRevenueModelForPlacement(placementId);
    }
}
