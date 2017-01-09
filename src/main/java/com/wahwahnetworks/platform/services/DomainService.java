package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.lib.analytics.AnalyticsRestTemplate;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.DomainManagementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jhaygood on 2/20/16.
 */

@Service
public class DomainService {

    @Autowired
    private AnalyticsService analyticsService;

    public List<DomainManagementModel> listDomainsForSite(SessionModel sessionModel, int siteId)  throws Exception {

        AnalyticsRestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);
        DomainManagementModel[] domains = restTemplate.getForObject("http://localhost:8080/analytics/api/1.0/domains/?site_id={siteId}",DomainManagementModel[].class,siteId);

        return Arrays.asList(domains);
    }

    public List<DomainManagementModel> listSuggestedDomains(SessionModel sessionModel, int siteId) throws Exception {

        AnalyticsRestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);
        DomainManagementModel[] domains = restTemplate.getForObject("http://localhost:8080/analytics/api/1.0/domains/suggested/?site_id={siteId}",DomainManagementModel[].class,siteId);

        return Arrays.asList(domains);
    }

    public void updateDomainsForSite(SessionModel sessionModel, int siteId, List<DomainManagementModel> domains) throws Exception {

        AnalyticsRestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);

        // Get Current Domains
        DomainManagementModel[] currentDomains = restTemplate.getForObject("http://localhost:8080/analytics/api/1.0/domains/?site_id={siteId}",DomainManagementModel[].class,siteId);

        List<DomainManagementModel> domainsToDelete = new ArrayList<>();

        for(DomainManagementModel domain : currentDomains){
            if(domains.stream().filter(d -> d.getDomain().equals(domain.getDomain())).count() == 0){
                domainsToDelete.add(domain);
            }
        }

        for(DomainManagementModel domainManagementModel : domainsToDelete){
            restTemplate.delete("http://localhost:8080/analytics/api/1.0/domains/" + domainManagementModel.getId());
        }

        restTemplate.put("http://localhost:8080/analytics/api/1.0/domains/",domains);

    }
}
