package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.data.entities.DemandSourceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 1/28/16.
 */
public interface DemandSourceConnectionRepository extends JpaRepository<DemandSourceConnection,Integer> {
    Page<DemandSourceConnection> findByDemandSourceId(int demandSourceId, Pageable pageable);
    Iterable<DemandSourceConnection> findByDemandSource(DemandSource demandSource);
    DemandSourceConnection findByDemandSourceIdAndId(int demandSourceId, int id);
}
