package com.wahwahnetworks.platform.services.tie;

import com.wahwahnetworks.platform.data.entities.LineItem;
import com.wahwahnetworks.platform.data.entities.LineItemAd;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.repos.LineItemAdRepository;
import com.wahwahnetworks.platform.data.repos.LineItemRepository;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceAdModel;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceLineItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by jhaygood on 6/7/16.
 */

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagIntelligenceLineItemExportService {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineItemAdRepository adRepository;

    @Autowired
    private TagIntelligenceCreativeExportService creativeExportService;

    @Autowired
    private TagIntelligenceService tagIntelligenceService;

    @Transactional
    public void exportLineItem(LineItem lineItem) throws Exception {

        List<String> ids = new ArrayList<>();

        for(LineItemAd ad : lineItem.getAds()){

            String creativeId = null;

            creativeExportService.exportCreative(ad.getCreative());

            if(ad.getCreative().getSecondaryAdServerType() == AdServerType.RED_PANDA_TIE){
                creativeId = ad.getCreative().getSecondaryAdServerId();
            }

            if(ad.getCreative().getPrimaryAdServerType() == AdServerType.RED_PANDA_TIE){
                creativeId = ad.getCreative().getPrimaryAdServerId();
            }

            if(creativeId == null){
                creativeId = ad.getCreative().getPrimaryAdServerId();
            }

            TagIntelligenceAdModel adModel = new TagIntelligenceAdModel();

            if(ad.getPrimaryAdServerType() == AdServerType.RED_PANDA_TIE){
                adModel.setId(ad.getPrimaryAdServerId());
            }

            adModel.setCreativeId(creativeId);
            adModel.setWeight(ad.getWeightPercentage());

            Future<TagIntelligenceAdModel> deferredAdModel = tagIntelligenceService.saveAd(adModel);
            adModel = deferredAdModel.get();

            ad.setPrimaryAdServerType(AdServerType.RED_PANDA_TIE);
            ad.setPrimaryAdServerId(adModel.getId());

            adRepository.save(ad);

            ids.add(adModel.getId());
        }

        TagIntelligenceLineItemModel lineItemModel = new TagIntelligenceLineItemModel();

        lineItemModel.setAds(ids);
        lineItemModel.setAdWeightingMode(lineItem.getAdWeightingMode());
        lineItemModel.setDeliveryMode(lineItem.getDeliveryMode());
        lineItemModel.setNominalCPM(lineItem.getExpectedCPMInCents());

        if(lineItem.getPrimaryAdServerType() == AdServerType.RED_PANDA_TIE){
            lineItemModel.setId(lineItem.getPrimaryAdServerId());
        }

        Future<TagIntelligenceLineItemModel> deferredLineItemModel = tagIntelligenceService.saveLineItem(lineItemModel);
        lineItemModel = deferredLineItemModel.get();

        lineItem.setPrimaryAdServerType(AdServerType.RED_PANDA_TIE);
        lineItem.setPrimaryAdServerId(lineItemModel.getId());

        lineItemRepository.save(lineItem);
    }
}
