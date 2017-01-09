package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jhaygood on 2/21/16.
 */
public interface OAuthTokenRepository extends JpaRepository<OAuthToken,Integer> {
}
