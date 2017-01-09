package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.*;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 3/31/16.
 */
public interface BillableEntityRepository extends CrudRepository<BillableEntity,Integer> {
    BillableEntity findByPublisher(AccountPublisher accountPublisher);
    BillableEntity findByNetwork(AccountNetwork accountNetwork);
    BillableEntity findBySite(Site site);
    BillableEntity findByProduct(Product product);
    BillableEntity findByPlacement(DemandSourcePlacement placement);
}
