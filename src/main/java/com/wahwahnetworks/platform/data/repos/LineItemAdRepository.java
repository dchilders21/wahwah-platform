package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.LineItemAd;
import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Brin.Bober on 1/5/2016.
 */
public interface LineItemAdRepository extends JpaRepository<LineItemAd, Integer>
{
	Iterable<LineItemAd> findAllByLineItemId(int id);

	LineItemAd findByPrimaryAdServerIdAndPrimaryAdServerType(String primaryAdServerId, AdServerType adServerType);
	LineItemAd findBySecondaryAdServerIdAndSecondaryAdServerType(String secondaryAdServerId, AdServerType adServerType);

	List<LineItemAd> findAllBySecondaryAdServerId(String secondaryAdServerId);
}
