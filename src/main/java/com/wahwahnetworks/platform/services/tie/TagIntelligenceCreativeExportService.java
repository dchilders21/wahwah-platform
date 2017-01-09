package com.wahwahnetworks.platform.services.tie;

import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.repos.CreativeRepository;
import com.wahwahnetworks.platform.models.tie.TagIntelligenceCreativeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Created by jhaygood on 6/7/16.
 */

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagIntelligenceCreativeExportService {

    @Autowired
    private CreativeRepository creativeRepository;

    @Autowired
    private TagIntelligenceService tagIntelligenceService;

    public void exportCreative(Creative creative) throws Exception {

        TagIntelligenceCreativeModel creativeModel = new TagIntelligenceCreativeModel();
        creativeModel.setCreativeType(creative.getCreativeType());
        creativeModel.setTagContents(creative.getTagContents());
        creativeModel.setVendor(creative.getVendorType());
        creativeModel.setCreativePlatform(creative.getCreativePlatform());

        if(creative.getPrimaryAdServerType() == AdServerType.RED_PANDA_TIE){
            creativeModel.setId(creative.getPrimaryAdServerId());
        }

        Future<TagIntelligenceCreativeModel> deferredCreativeModel = tagIntelligenceService.saveCreative(creativeModel);
        creativeModel = deferredCreativeModel.get();

        creative.setPrimaryAdServerType(AdServerType.RED_PANDA_TIE);
        creative.setPrimaryAdServerId(creativeModel.getId());

        creativeRepository.save(creative);

    }
}
