package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Integer>
{
	// Find By Network
	Page<Site> findByPublisherParentAccountIdAndIsArchivedIsFalseAndIsDefaultIsFalseOrderBySiteNameAsc(Integer accountId, Pageable pageable);

	// Find By Publisher
	Page<Site> findByPublisherIdAndIsArchivedIsFalseAndIsDefaultIsFalseOrderBySiteNameAsc(Integer publisherId, Pageable pageable);

	List<Site> findByPublisher(AccountPublisher publisher);

	Site findByPublisherAndSiteName(AccountPublisher publisher, String siteName);

	Site findById(int id);

	Iterable<Site> findBySiteNameContainingOrderBySiteNameAsc(String searchTerm);

	Iterable<Site> findBySiteName(String siteName);

	Site findByMarketplaceSite(Site marketplaceSite);

	int countByPublisherParentAccountAndIsArchivedIsFalse(Account account);

	int countByPublisherAndIsArchivedFalse(AccountPublisher publisher);
}
