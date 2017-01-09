package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.OAuthApplication;
import com.wahwahnetworks.platform.data.entities.OAuthGrant;
import com.wahwahnetworks.platform.data.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 3/2/15.
 */
public interface OAuthGrantRepository extends CrudRepository<OAuthGrant, Integer>
{
	OAuthGrant findByApplicationAndUser(OAuthApplication application, User user);
}
