package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.OAuthApplication;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 3/2/15.
 */
public interface OAuthApplicationRepository extends CrudRepository<OAuthApplication, Integer>
{
	OAuthApplication findByClientId(String clientId);
}
