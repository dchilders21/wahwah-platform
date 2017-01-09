package com.wahwahnetworks.platform.controllers;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.services.DemandSourceService;
import com.wahwahnetworks.platform.services.PlatformService;
import com.wahwahnetworks.platform.services.PublisherService;
import com.wahwahnetworks.platform.services.SiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Justin on 3/25/2014.
 */

@Controller
@RequestMapping("/")
@Scope("request")
public class PlatformController
{

	private static final Logger log = Logger.getLogger(PlatformController.class);

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private PlatformService platformService;

    @Autowired
    private DemandSourceService demandSourceService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private SiteService siteService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.USER)
	public String showPlatform(ModelMap modelMap, HttpServletRequest httpServletRequest)
	{
		String serverName = httpServletRequest.getServerName();
		String commitId = platformService.getCommitId();

		UserModel userModel = sessionModel.getUser();
		modelMap.put("user", userModel);
		modelMap.put("version", platformService.getPlatformVersion());
		modelMap.put("commitId", commitId);
		modelMap.put("serverName",serverName);
        modelMap.put("defaultPath","overview");
		modelMap.put("defaultDashboard","");

        if(userModel.getAccountType() == AccountType.FREE){
            int demandSourceCount = demandSourceService.getDemandSourceCountForAccountId(userModel.getAccountId());

            if(demandSourceCount == 0) {
                modelMap.put("defaultPath", "demand-sources/create");
            }

            modelMap.put("defaultDashboard","demand_source");
        }

		if(userModel.getAccountType() == AccountType.ROOT || userModel.getAccountType() == AccountType.NETWORK || userModel.getAccountType() == AccountType.REPORTING_PRO)
		{
			int publisherCount = publisherService.getPublisherCountForAccountId(userModel.getAccountId());
            int siteCount = 0;

            if(publisherCount != 0){
                siteCount = siteService.getSiteCountForAccountId(userModel.getAccountId());
            }

            if(publisherCount > 0 && siteCount > 0){
                modelMap.put("defaultDashboard","network");
            } else {
                modelMap.put("defaultDashboard","demand_source");
            }
		}

        if(userModel.getAccountType() == AccountType.PUBLISHER)
        {
            modelMap.put("defaultDashboard","publisher");
        }

		return "platform/platform";
	}
}
