package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.models.facebook.FacebookGraphInsightsModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Justin on 6/10/2014.
 */

@Service
public class FacebookInsightsService
{

	private static final String FACEBOOK_APP_ID = "456328617828956";
	private static final String FACEBOOK_APP_SECRET = "5f02e2b08bdaeaa94ec5c2973c9b36a6";

	public FacebookGraphInsightsModel getFacebookInsights()
	{

		RestTemplate facebookGraphInsightsTemplate = new RestTemplate();
		return facebookGraphInsightsTemplate.getForObject("https://graph.facebook.com/v2.0/" + FACEBOOK_APP_ID + "/insights?access_token=" + FACEBOOK_APP_ID + "|" + FACEBOOK_APP_SECRET, FacebookGraphInsightsModel.class);
	}
}
