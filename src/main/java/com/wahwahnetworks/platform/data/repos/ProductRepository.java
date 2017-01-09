package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.Product;
import com.wahwahnetworks.platform.data.entities.ProductVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>
{
	Iterable<Product> findBySiteId(int siteId);

	Page<Product> findBySitePublisherId(int publisherId, Pageable pageable);

	Iterable<Product> findByProductVersion(ProductVersion productVersion);

	Product findByWidgetId(int widgetId);

	Product findById(int num);

	Iterable<Product> findByNameContainingOrderByNameAsc(String searchTerm);
}
