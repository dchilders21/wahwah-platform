package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.ClientFeature;
import com.wahwahnetworks.platform.data.repos.ClientFeatureRepository;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.models.web.ClientFeatureWebListModel;
import com.wahwahnetworks.platform.models.web.ClientFeatureWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 11/13/15.
 */

@Service
public class ClientFeatureService {

    @Autowired
    private ClientFeatureRepository clientFeatureRepository;

    @Transactional
    public ClientFeatureWebModel createNewClientFeature(ClientFeatureWebModel clientFeatureWebModel){
        ClientFeature clientFeature = getClientFeatureFromModel(clientFeatureWebModel);
        clientFeatureRepository.save(clientFeature);
        return getModelFromClientFeature(clientFeature);
    }

    @Transactional
    public ClientFeatureWebModel updateExistingClientFeature(ClientFeatureWebModel clientFeatureWebModel){
        ClientFeature clientFeature = getClientFeatureFromModel(clientFeatureWebModel);
        clientFeatureRepository.save(clientFeature);
        return getModelFromClientFeature(clientFeature);
    }

    @Transactional(readOnly = true)
    public ClientFeatureWebModel getClientFeatureById(int clientFeatureId){
        ClientFeature clientFeature = clientFeatureRepository.findOne(clientFeatureId);
        return getModelFromClientFeature(clientFeature);
    }

    @Transactional(readOnly = true)
    public ClientFeatureWebListModel listClientFeatures(){
        Iterable<ClientFeature> clientFeatures = clientFeatureRepository.findAll();

        List<ClientFeatureWebModel> clientFeatureWebListModels = new ArrayList<>();

        for(ClientFeature clientFeature : clientFeatures){
            ClientFeatureWebModel clientFeatureWebModel = getModelFromClientFeature(clientFeature);
            clientFeatureWebListModels.add(clientFeatureWebModel);
        }

        ClientFeatureWebListModel clientFeatureWebListModel = new ClientFeatureWebListModel();
        clientFeatureWebListModel.setClientFeatures(clientFeatureWebListModels);

        return clientFeatureWebListModel;
    }

    @Transactional
    public void deleteClientFeatureById(int clientFeatureId){
        clientFeatureRepository.delete(clientFeatureId);
    }

    private ClientFeature getClientFeatureFromModel(ClientFeatureWebModel clientFeatureWebModel){

        ClientFeature clientFeature;

        if(clientFeatureWebModel.getId() != 0){
            clientFeature = clientFeatureRepository.findOne(clientFeatureWebModel.getId());
        } else {
            clientFeature = new ClientFeature();
        }

        clientFeature.setName(clientFeatureWebModel.getName());
        clientFeature.setDescription(clientFeatureWebModel.getDescription());
        clientFeature.setClientVariableName(clientFeatureWebModel.getVariableName());
        clientFeature.setClientVariableType(clientFeatureWebModel.getVariableType());
        clientFeature.setClientVariableLabel(clientFeatureWebModel.getVariableLabel());

        return clientFeature;
    }

    private ClientFeatureWebModel getModelFromClientFeature(ClientFeature clientFeature){
        ClientFeatureWebModel clientFeatureWebModel = new ClientFeatureWebModel();
        clientFeatureWebModel.setId(clientFeature.getId());
        clientFeatureWebModel.setName(clientFeature.getName());
        clientFeatureWebModel.setDescription(clientFeature.getDescription());
        clientFeatureWebModel.setVariableName(clientFeature.getClientVariableName());
        clientFeatureWebModel.setVariableType(clientFeature.getClientVariableType());
        clientFeatureWebModel.setVariableLabel(clientFeature.getClientVariableLabel());
        return clientFeatureWebModel;
    }
}
