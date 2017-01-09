package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.*;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by Brian.Bober on 4/18/2016.
 */
public interface LineItemTargetingRepository extends CrudRepository<LineItemTargeting,Integer>
{
	Collection<LineItemTargeting> findByLineItem(LineItem lineItem);

	List<LineItemTargeting> findByProduct(Product product);
	List<LineItemTargeting> findBySite(Site site);
	List<LineItemTargeting> findByPublisher(AccountPublisher publisher);
	List<LineItemTargeting> findByNetwork(AccountNetwork network);
}
