package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.analytics.CustomReportRequestModel;
import com.wahwahnetworks.platform.models.analytics.CustomReportResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jhaygood on 4/18/16.
 */

@Service
public class CustomReportService {

    @Autowired
    private AnalyticsService analyticsService;

    public CustomReportResponseModel runReport(SessionModel sessionModel, CustomReportRequestModel customReportRequestModel) throws Exception {
        RestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);
        return restTemplate.postForObject("http://localhost:8080/analytics/api/1.0/custom-reports/run",customReportRequestModel,CustomReportResponseModel.class);
    }
}
