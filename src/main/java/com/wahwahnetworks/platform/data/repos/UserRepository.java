package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Justin on 5/12/2014.
 */

public interface UserRepository extends JpaRepository<User, Integer>
{
	User findByEmailAddress(String emailAddress);

	Page<User> findByAccountId(int accountId, Pageable pageable);
}
