package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.lib.MoneyUtils;
import com.wahwahnetworks.platform.models.BillableEntityModel;
import com.wahwahnetworks.platform.models.web.RevenueWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jhaygood on 3/31/16.
 */

@Service
public class RevenueModelService {

    @Autowired
    private BillableEntityRepository billableEntityRepository;

    @Autowired
    private AccountPublisherRepository accountPublisherRepository;

    @Autowired
    private AccountNetworkRepository accountNetworkRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DemandSourcePlacementRepository placementRepository;

    @Autowired
    private RevenueModelRepository revenueModelRepository;

    @Transactional
    public void updateRevenueModel(BillableEntity billableEntity, RevenueWebModel revenueWebModel){

        RevenueModel revenueModel;

        if(revenueWebModel.getId() == null){
            revenueModel = new RevenueModel(billableEntity,revenueWebModel.getRevenueModelType());
        } else {
            revenueModel = revenueModelRepository.findOne(revenueWebModel.getId());

            if(revenueModel.getPublisher() != null){

                AccountPublisher publisher = revenueModel.getPublisher();
                BillableEntity publisherBillableEntity = getOrCreateBillableEntity(new BillableEntityModel() {
                    @Override
                    public BillableEntityType getBillableEntityTargetType() {
                        return BillableEntityType.PUBLISHER;
                    }

                    @Override
                    public Integer getBillableEntityTargetId() {
                        return publisher.getId();
                    }
                });

                revenueModel.setPublisher(null);
                revenueModel.setBillableEntity(publisherBillableEntity);
            }

            revenueModelRepository.save(revenueModel);
        }

        revenueModel.setStartDate(revenueWebModel.getEffectiveStartDate());
        revenueModel.setEndDate(revenueWebModel.getEffectiveEndDate());
        revenueModel.setRevenueModelType(revenueWebModel.getRevenueModelType());
        revenueModel.setGuaranteedCPMForUSDesktopImpressions(MoneyUtils.getCentsForDollarString(revenueWebModel.getGuaranteedCPMForUSDesktop()));
        revenueModel.setGuaranteedCPMForOtherImpressions(MoneyUtils.getCentsForDollarString(revenueWebModel.getGuaranteedCPMForOther()));
        revenueModel.setRevenueSharePercentage(Integer.parseInt(revenueWebModel.getRevenueSharePercentage()));
        revenueModel.setMinimumImpressions(revenueWebModel.getMinimumRequests());

        if(revenueWebModel.getMinimumPayout() != null){
            revenueModel.setMinimumRevenueInCents(revenueWebModel.getMinimumPayout() * 100);
        }

        revenueModelRepository.save(revenueModel);
    }

    @Transactional(readOnly = true)
    public RevenueModel getRevenueModelForPublisher(AccountPublisher accountPublisher)
    {
        BillableEntity billableEntity = billableEntityRepository.findByPublisher(accountPublisher);

        if(billableEntity == null){
            billableEntity = createBillableEntityForPublisher(accountPublisher);
        }

        return getRevenueModel(billableEntity);
    }

    @Transactional(readOnly = true)
    public RevenueModel getRevenueModelForProduct(Product product){

        BillableEntity billableEntity = billableEntityRepository.findByProduct(product);

        if(billableEntity == null){
            billableEntity = createBillableEntityForProduct(product);
        }

        return getRevenueModel(billableEntity);
    }

    @Transactional(readOnly = true)
    public RevenueModel getRevenueModel(BillableEntity billableEntity)
    {
        RevenueModel revenueModel = null;

        if(billableEntity != null && billableEntity.getId() != 0){
            revenueModel = revenueModelRepository.findByBillableEntityAndEndDateIsNull(billableEntity);
        }

        if(revenueModel == null && billableEntity != null && billableEntity.getEntityType() == BillableEntityType.PUBLISHER) {
            revenueModel = revenueModelRepository.findByPublisherAndEndDateIsNull(billableEntity.getPublisher());
        }

        return revenueModel;
    }

    private BillableEntity getForBillableEntity(BillableEntityModel billableEntityModel){

        if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.PUBLISHER){
            AccountPublisher billablePublisher = accountPublisherRepository.findOne(billableEntityModel.getBillableEntityTargetId());
            return billableEntityRepository.findByPublisher(billablePublisher);
        }

        if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.NETWORK){
            AccountNetwork billableNetwork = accountNetworkRepository.findOne(billableEntityModel.getBillableEntityTargetId());
            return billableEntityRepository.findByNetwork(billableNetwork);
        }

        if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.SITE){
            Site billableSite = siteRepository.findOne(billableEntityModel.getBillableEntityTargetId());
            return billableEntityRepository.findBySite(billableSite);
        }

        if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.PRODUCT){
            Product billableProduct = productRepository.findOne(billableEntityModel.getBillableEntityTargetId());
            return billableEntityRepository.findByProduct(billableProduct);
        }

        if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.DEMAND_SOURCE_PLACEMENT){
            DemandSourcePlacement billablePlacement = placementRepository.findOne(billableEntityModel.getBillableEntityTargetId());
            return billableEntityRepository.findByPlacement(billablePlacement);
        }

        return null;
    }

    @Transactional
    public BillableEntity getOrCreateBillableEntity(BillableEntityModel billableEntityModel){
        BillableEntity billableEntity = getForBillableEntity(billableEntityModel);

        if(billableEntity == null){
            if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.PUBLISHER){
                AccountPublisher billablePublisher = accountPublisherRepository.findOne(billableEntityModel.getBillableEntityTargetId());
                billableEntity = createBillableEntityForPublisher(billablePublisher);
            }

            if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.NETWORK){
                AccountNetwork billableNetwork = accountNetworkRepository.findOne(billableEntityModel.getBillableEntityTargetId());
                billableEntity = createBillableEntityForNetwork(billableNetwork);
            }

            if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.SITE){
                Site billableSite = siteRepository.findOne(billableEntityModel.getBillableEntityTargetId());
                billableEntity = createBillableEntityForSite(billableSite);
            }

            if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.PRODUCT){
                Product billableProduct = productRepository.findOne(billableEntityModel.getBillableEntityTargetId());
                billableEntity = createBillableEntityForProduct(billableProduct);
            }

            if(billableEntityModel.getBillableEntityTargetType() == BillableEntityType.DEMAND_SOURCE_PLACEMENT){
                DemandSourcePlacement billablePlacement = placementRepository.findOne(billableEntityModel.getBillableEntityTargetId());
                billableEntity = createBillableEntityForPlacement(billablePlacement);
            }

            billableEntityRepository.save(billableEntity);
        }

        return billableEntity;
    }

    @Transactional
    public List<RevenueModel> getRevenueModelsForBillableEntity(BillableEntity billableEntity){

        List<RevenueModel> revenueModels = new ArrayList<>();

        if(billableEntity.getEntityType() == BillableEntityType.PUBLISHER){

            RevenueModel model = revenueModelRepository.findByPublisherAndEndDateIsNull(billableEntity.getPublisher());

            if(model != null){
                revenueModels.add(model);
            }
        }

        revenueModels.addAll(revenueModelRepository.findByBillableEntity(billableEntity));

        Collections.sort(revenueModels, (RevenueModel model1, RevenueModel model2) -> {
            LocalDate startDate1 = model1.getStartDate();
            LocalDate startDate2 = model2.getStartDate();

            if(startDate1 == null && startDate2 == null){
                return 0;
            }

            if(startDate1 == null && startDate2 != null){
                return -1;
            }

            if(startDate1 != null && startDate2 == null){
                return 1;
            }

            if (startDate1.isBefore(startDate2)) {
                return -1;
            } else if (startDate1.isEqual(startDate2)){
                return 0;
            } else {
                return 1;
            }
        });

        if(revenueModels.size() > 0){
            revenueModels.get(0).setStartDate(null);
        }

        revenueModelRepository.save(revenueModels);

        return revenueModels;
    }

    private void deleteBillableEntity(BillableEntity billableEntity){
        if(billableEntity != null){
            RevenueModel revenueModel = getRevenueModel(billableEntity);

            if(revenueModel != null){
                revenueModelRepository.delete(revenueModel);
            }

            billableEntityRepository.delete(billableEntity);
        }
    }

    @Transactional
    public void deleteBillableEntityForNetwork(AccountNetwork accountNetwork){
        BillableEntity billableEntity = billableEntityRepository.findByNetwork(accountNetwork);
        deleteBillableEntity(billableEntity);
    }

    @Transactional
    public void deleteBillableEntityForPublisher(AccountPublisher accountPublisher){

        BillableEntity billableEntity = billableEntityRepository.findByPublisher(accountPublisher);

        if(billableEntity == null){
            billableEntity = createBillableEntityForPublisher(accountPublisher);
        }

        deleteBillableEntity(billableEntity);
    }

    @Transactional
    public void deleteBillableEntityForSite(Site site){
        BillableEntity billableEntity = billableEntityRepository.findBySite(site);
        deleteBillableEntity(billableEntity);
    }

    @Transactional
    public void deleteBillableEntityForProduct(Product product){
        BillableEntity billableEntity = billableEntityRepository.findByProduct(product);
        deleteBillableEntity(billableEntity);
    }

    private BillableEntity createBillableEntityForPublisher(AccountPublisher accountPublisher){
        BillableEntity billableEntity = new BillableEntity();
        billableEntity.setEntityType(BillableEntityType.PUBLISHER);
        billableEntity.setPublisher(accountPublisher);
        return billableEntity;
    }

    private BillableEntity createBillableEntityForNetwork(AccountNetwork accountNetwork){
        BillableEntity billableEntity = new BillableEntity();
        billableEntity.setEntityType(BillableEntityType.NETWORK);
        billableEntity.setNetwork(accountNetwork);
        return billableEntity;
    }

    private BillableEntity createBillableEntityForSite(Site site){
        BillableEntity billableEntity = new BillableEntity();
        billableEntity.setEntityType(BillableEntityType.SITE);
        billableEntity.setSite(site);
        return billableEntity;
    }

    private BillableEntity createBillableEntityForProduct(Product product){
        BillableEntity billableEntity = new BillableEntity();
        billableEntity.setEntityType(BillableEntityType.PRODUCT);
        billableEntity.setProduct(product);
        return billableEntity;
    }

    private BillableEntity createBillableEntityForPlacement(DemandSourcePlacement placement){
        BillableEntity billableEntity = new BillableEntity();
        billableEntity.setEntityType(BillableEntityType.DEMAND_SOURCE_PLACEMENT);
        billableEntity.setPlacement(placement);
        return billableEntity;
    }
}
