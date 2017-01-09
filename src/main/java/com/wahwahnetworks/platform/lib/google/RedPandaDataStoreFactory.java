package com.wahwahnetworks.platform.lib.google;

import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by jhaygood on 4/11/16.
 */

@Component
public class RedPandaDataStoreFactory extends AbstractDataStoreFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {

        if(id.equals("StoredCredential")){
            return (DataStore<V>)applicationContext.getBean("googleStoredCredentialDataStore",this,id);
        }

        return applicationContext.getBean(DataStore.class,this,id);
    }
}
