package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.BillableEntity;
import com.wahwahnetworks.platform.data.entities.RevenueModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

/**
 * Created by justin on 4/24/15.
 */
public interface RevenueModelRepository extends CrudRepository<RevenueModel, Integer>
{
	RevenueModel findByPublisherAndEndDateIsNull(AccountPublisher publisher);
	RevenueModel findByBillableEntityAndEndDateIsNull(BillableEntity billableEntity);

	Collection<RevenueModel> findByBillableEntity(BillableEntity billableEntity);
}
