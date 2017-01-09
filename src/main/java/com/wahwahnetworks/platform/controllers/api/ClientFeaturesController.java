package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.models.web.ClientFeatureWebListModel;
import com.wahwahnetworks.platform.models.web.ClientFeatureWebModel;
import com.wahwahnetworks.platform.services.ClientFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * Created by jhaygood on 11/13/15.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/client-features/")
public class ClientFeaturesController extends BaseAPIController {

    @Autowired
    private ClientFeatureService clientFeatureService;

    @RequestMapping(method = RequestMethod.GET)
    @HasUserRole(UserRoleType.INTERNAL_USER)
    public ClientFeatureWebListModel listClientFeatures(){
        return clientFeatureService.listClientFeatures();
    }

    @RequestMapping(method = RequestMethod.POST)
    @HasUserRole(UserRoleType.DEVELOPER)
    public ClientFeatureWebModel createClientFeature(@RequestBody ClientFeatureWebModel clientFeatureWebModel){

        if(clientFeatureWebModel.getId() != 0){
            throw new ModelValidationException("ID in request body is already set");
        }

        return clientFeatureService.createNewClientFeature(clientFeatureWebModel);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{clientFeatureId}")
    @HasUserRole(UserRoleType.INTERNAL_USER)
    public ClientFeatureWebModel getClientFeature(@PathVariable int clientFeatureId){
        ClientFeatureWebModel clientFeatureWebModel = clientFeatureService.getClientFeatureById(clientFeatureId);
        return clientFeatureWebModel;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{clientFeatureId}")
    @HasUserRole(UserRoleType.DEVELOPER)
    public ClientFeatureWebModel updateClientFeature(@PathVariable int clientFeatureId, @RequestBody ClientFeatureWebModel clientFeatureWebModel){

        if(clientFeatureId !=  clientFeatureWebModel.getId()){
            throw new ModelValidationException("ID in path doesn't match ID field of request body");
        }

        return clientFeatureService.updateExistingClientFeature(clientFeatureWebModel);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{clientFeatureId}")
    @HasUserRole(UserRoleType.DEVELOPER)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteClientFeature(@PathVariable int clientFeatureId){
        clientFeatureService.deleteClientFeatureById(clientFeatureId);
    }
}
