package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.data.entities.enums.CreativeType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.CreativeVendorListModel;
import com.wahwahnetworks.platform.services.CreativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by jhaygood on 4/14/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/creatives/")
public class CreativeController {

    @Autowired
    private CreativeService creativeService;

    @Autowired
    private SessionModel sessionModel;

    @RequestMapping(method = RequestMethod.GET, value = "vendors/{creativeType}")
    public CreativeVendorListModel getTypesForType(@PathVariable CreativeType creativeType) throws IOException {
        return creativeService.getVendorsForType(creativeType);
    }
}
