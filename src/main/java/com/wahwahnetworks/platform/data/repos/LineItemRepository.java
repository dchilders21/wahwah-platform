package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.data.entities.LineItem;
import com.wahwahnetworks.platform.data.entities.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Brin.Bober on 12/9/2015.
 */
public interface LineItemRepository extends JpaRepository<LineItem, Integer>
{
	List<LineItem> findAllBySite(Site site);

	List<LineItem> findByDemandSource(DemandSource demandSource);

	List<LineItem> findByDemandSourceAccountAndDemandSourceIsNotNull(Account account);
}
