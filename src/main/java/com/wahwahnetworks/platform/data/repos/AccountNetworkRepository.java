package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 1/26/16.
 */
public interface AccountNetworkRepository extends JpaRepository<AccountNetwork,Integer> {
    Iterable<AccountNetwork> findByNameStartsWith(String searchString);
    Page<AccountNetwork> findByParentAccount(Account parentAccount, Pageable pageable);
    AccountNetwork findByName(String accountName);
    Iterable<AccountNetwork> findByNameContainingOrderByNameAsc(String searchTerm);
}
