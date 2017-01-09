package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.GoogleDataStore;
import com.wahwahnetworks.platform.data.entities.GoogleDataStoreValue;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jhaygood on 4/11/16.
 */
public interface GoogleDataStoreValueRepository extends CrudRepository<GoogleDataStoreValue,Integer> {
    List<GoogleDataStoreValue> findByDataStore(GoogleDataStore dataStore);
    int countByDataStore(GoogleDataStore dataStore);

    GoogleDataStoreValue findByDataStoreAndKey(GoogleDataStore dataStore, String key);
}
