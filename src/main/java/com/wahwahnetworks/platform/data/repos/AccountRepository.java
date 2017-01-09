package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer>
{
	Iterable<Account> findByNameStartsWith(String searchString);
	Account findByName(String accountName);

	@Query("SELECT a FROM Account a WHERE TYPE(a) IN :accountTypes")
	Page<Account> findByParentAccountIsNullAndAccountTypeIn(@Param("accountTypes") List<Class> accountTypes, Pageable pageable);
}
