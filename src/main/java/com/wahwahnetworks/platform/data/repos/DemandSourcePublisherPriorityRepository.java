package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.DemandSourcePublisherPriority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jhaygood on 4/20/16.
 */
public interface DemandSourcePublisherPriorityRepository extends CrudRepository<DemandSourcePublisherPriority,Integer> {
    List<DemandSourcePublisherPriority> findAllByNetworkAndPublisherOrderByPriorityAsc(AccountNetwork network, AccountPublisher publisher);
}
