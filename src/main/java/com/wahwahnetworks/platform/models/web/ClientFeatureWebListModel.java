package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.ClientFeature;

import java.util.List;

/**
 * Created by jhaygood on 11/13/15.
 */

@WebSafeModel
public class ClientFeatureWebListModel {
    private List<ClientFeatureWebModel> clientFeatures;

    public List<ClientFeatureWebModel> getClientFeatures() {
        return clientFeatures;
    }

    public void setClientFeatures(List<ClientFeatureWebModel> clientFeatures) {
        this.clientFeatures = clientFeatures;
    }
}
