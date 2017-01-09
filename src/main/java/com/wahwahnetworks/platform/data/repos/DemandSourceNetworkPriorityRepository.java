package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.DemandSourceNetworkPriority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jhaygood on 4/20/16.
 */
public interface DemandSourceNetworkPriorityRepository extends CrudRepository<DemandSourceNetworkPriority,Integer> {
    List<DemandSourceNetworkPriority> findAllByNetworkOrderByPriorityAsc(AccountNetwork network);
}
