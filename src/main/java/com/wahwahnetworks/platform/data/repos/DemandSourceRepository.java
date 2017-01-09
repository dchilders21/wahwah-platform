package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 1/27/16.
 */
public interface DemandSourceRepository extends JpaRepository<DemandSource,Integer> {
    Iterable<DemandSource> findByAccountOrderByName(Account account);
    Page<DemandSource> findByAccount(Account account, Pageable pageable);

    DemandSource findByName(String name);

    int countByAccount(Account account);
}
