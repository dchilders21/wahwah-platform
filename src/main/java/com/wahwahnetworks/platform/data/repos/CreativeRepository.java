package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 4/14/16.
 */
public interface CreativeRepository extends CrudRepository<Creative,Integer> {
    Creative findByPrimaryAdServerIdAndPrimaryAdServerType(String primaryAdServerId, AdServerType adServerType);
    Creative findBySecondaryAdServerIdAndSecondaryAdServerType(String secondaryAdServerId, AdServerType adServerType);
    Iterable<Creative> findByDemandSource(DemandSource demandSource);
}
