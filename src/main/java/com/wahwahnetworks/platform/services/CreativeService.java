package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.data.entities.DemandSourceConnectionType;
import com.wahwahnetworks.platform.data.entities.enums.CreativePlatform;
import com.wahwahnetworks.platform.data.entities.enums.CreativeType;
import com.wahwahnetworks.platform.data.repos.CreativeRepository;
import com.wahwahnetworks.platform.data.repos.DemandSourceRepository;
import com.wahwahnetworks.platform.models.web.CreativeVendorListModel;
import com.wahwahnetworks.platform.models.web.CreativeVendorModel;
import com.wahwahnetworks.platform.models.web.DemandSourceCreativeModel;
import com.wahwahnetworks.platform.models.web.DemandSourceModel;
import com.wahwahnetworks.platform.services.tie.TagIntelligenceCreativeExportService;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhaygood on 4/14/16.
 */

@Service
public class CreativeService {

    @Autowired
    private DemandSourceRepository demandSourceRepository;

    @Autowired
    private CreativeRepository creativeRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TagIntelligenceCreativeExportService creativeExportService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DemandSourceService demandSourceService;

    public CreativeVendorListModel getVendorsForType(CreativeType creativeType) throws IOException {
        List<CreativeVendorModel> creativeVendorModelList = new ArrayList<>();

        Resource creativeVendorResource;

        switch (creativeType){
            case HTML:
                creativeVendorResource = applicationContext.getResource("classpath:data/display_vendors.txt");
                break;
            case VAST_XML:
                creativeVendorResource = applicationContext.getResource("classpath:data/vast_vendors.txt");
                break;
            default:
                return new CreativeVendorListModel();
        }

        List<String> creativeVendorResourceList = IOUtils.readLines(creativeVendorResource.getInputStream(), Charset.forName("UTF-8"));

        for(String creativeVendorData : creativeVendorResourceList){

            String creativeVendorType = creativeVendorData.split(";")[0];
            DemandSourceConnectionType type = demandSourceService.getConnectionTypeForKey(creativeVendorType);

            CreativeVendorModel vendorModel = new CreativeVendorModel();
            vendorModel.setType(creativeVendorType);
            vendorModel.setName(type.getName());



            creativeVendorModelList.add(vendorModel);
        }

        CreativeVendorListModel listModel = new CreativeVendorListModel();
        listModel.setCreativeVendors(creativeVendorModelList);
        return listModel;
    }

    @Transactional
    public List<DemandSourceCreativeModel> getCreativesForDemandSourceId(int demandSourceId){

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        Iterable<Creative> creatives = creativeRepository.findByDemandSource(demandSource);

        List<DemandSourceCreativeModel> creativeModels = new ArrayList<>();

        for(Creative creative : creatives){
            DemandSourceCreativeModel creativeModel = new DemandSourceCreativeModel(creative);
            creativeModels.add(creativeModel);
        }

        return creativeModels;
    }

    @Transactional
    public Map<Integer,Integer> saveCreatives(DemandSourceModel demandSourceModel, List<DemandSourceCreativeModel> creativeModels) throws Exception {

        Map<Integer,Integer> idMap = new HashMap<>();

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceModel.getDemandSourceId());

        Iterable<Creative> allCreatives = creativeRepository.findByDemandSource(demandSource);

        for(Creative creative : allCreatives){
            if(creativeModels.stream().filter(c -> c.getId() == creative.getId()).count() == 0){
                creativeRepository.delete(creative);
            }
        }

        for(DemandSourceCreativeModel creativeModel : creativeModels){

            Creative creative = null;

            if(creativeModel.getId() != null && creativeModel.getId() > 0){
                creative = creativeRepository.findOne(creativeModel.getId());
            }

            if(creative == null){
                creative = new Creative();
                creative.setDemandSource(demandSource);
                creative.setCreativeType(creativeModel.getContentType());
            }

            creative.setName(creativeModel.getName());
            creative.setVendorType(creativeModel.getVendorType());
            creative.setCreativePlatform(creativeModel.getCreativePlatform());

            switch(creativeModel.getContentType()){
                case HTML:
                    creative.setTagContents(creativeModel.getContentSnippet());
                    break;
                case VAST_XML:
                    creative.setTagContents(creativeModel.getContentUrl());
                    break;
            }

            creativeRepository.save(creative);
            creativeExportService.exportCreative(creative);

            idMap.put(creativeModel.getId(),creative.getId());
        }

        return idMap;
    }
}
