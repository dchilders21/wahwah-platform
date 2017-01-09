package com.wahwahnetworks.platform.lib.google;

import com.google.api.client.auth.oauth2.StoredCredential;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by jhaygood on 4/12/16.
 */

@Component("googleStoredCredentialDataStore")
@Scope("prototype")
public class RedPandaGoogleStoredCredentialDataStore extends RedPandaDataStore<StoredCredential> {
    public RedPandaGoogleStoredCredentialDataStore(RedPandaDataStoreFactory dataStoreFactory, String id) {
        super(dataStoreFactory, id);
    }

    @Override
    protected Class<StoredCredential> getClassType(){
        return StoredCredential.class;
    }
}
