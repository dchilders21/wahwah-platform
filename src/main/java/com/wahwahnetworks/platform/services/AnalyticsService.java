package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.lib.analytics.AnalyticsRestTemplate;
import com.wahwahnetworks.platform.models.SessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jhaygood on 2/20/16.
 */

@Service
public class AnalyticsService {

    @Autowired
    private OAuthService oAuthService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AnalyticsRestTemplate getRestTemplateForUser(SessionModel sessionModel) throws Exception {
        String bearerToken = sessionModel.getPlatformOAuthToken();
        return new AnalyticsRestTemplate(bearerToken);
    }
}
