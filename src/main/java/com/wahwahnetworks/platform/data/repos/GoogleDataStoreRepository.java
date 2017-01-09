package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.GoogleDataStore;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 4/11/16.
 */
public interface GoogleDataStoreRepository extends CrudRepository<GoogleDataStore,Integer> {
    GoogleDataStore findByGoogleValueId(String id);

}
