package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.services.SiteService;
import com.wahwahnetworks.platform.services.SiteTagMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jhaygood on 1/14/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/site-tag-migration/")
public class SiteTagMigrationController extends BaseAPIController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteTagMigrationService siteTagMigrationService;

    @Autowired
    private SessionModel sessionModel;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @HasUserRole(UserRoleType.DEVELOPER)
    public void migrateSite(@RequestParam(value = "old_site_id") int oldSiteId, @RequestParam(value = "new_site_id") int newSiteId) throws Exception {

        Site oldSite = siteService.getSiteEntityById(oldSiteId,sessionModel.getUser());
        Site newSite = siteService.getSiteEntityById(newSiteId,sessionModel.getUser());

        siteTagMigrationService.migrateSiteAndTags(sessionModel.getUser(),oldSite,newSite);
    }
}
