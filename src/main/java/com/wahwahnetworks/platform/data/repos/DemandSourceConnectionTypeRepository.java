package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.DemandSourceConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 1/27/16.
 */
public interface DemandSourceConnectionTypeRepository extends JpaRepository<DemandSourceConnectionType,Integer> {
    DemandSourceConnectionType findByTypeKey(String typeKey);
}
