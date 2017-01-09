package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jhaygood on 1/26/16.
 */
public interface AccountPublisherRepository extends JpaRepository<AccountPublisher,Integer> {
    Page<AccountPublisher> findByParentAccount(Account parentAccount, Pageable pageable);
    List<AccountPublisher> findByParentAccount(Account parentAccount);
    Page<AccountPublisher> findByParentAccountAndIsDefaultIsFalse(Account parentAccount, Pageable pageable);
    Page<AccountPublisher> findByIsDefaultIsFalse(Pageable pageable);
    Iterable<AccountPublisher> findByNameStartsWith(String searchString);
    Iterable<AccountPublisher> findByNameStartsWithAndIsDefaultIsFalse(String searchString);
    AccountPublisher findByName(String accountName);
    Iterable<AccountPublisher> findByNameContainingOrderByNameAsc(String searchTerm);
    AccountPublisher findByMarketplacePublisher(AccountPublisher marketplacePublisher);
    int countByParentAccount(Account account);
}
