package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.data.entities.DemandSourcePlacement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 7/20/16.
 */
public interface DemandSourcePlacementRepository extends JpaRepository<DemandSourcePlacement,Integer> {
    Iterable<DemandSourcePlacement> findByDemandSource(DemandSource demandSource);
}
