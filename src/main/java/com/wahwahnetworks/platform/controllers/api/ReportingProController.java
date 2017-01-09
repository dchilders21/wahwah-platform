package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.ReportingProAccountWebModel;
import com.wahwahnetworks.platform.services.ReportingProAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jhaygood on 7/14/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/reporting-pro-accounts")
public class ReportingProController extends BaseAPIController {

    @Autowired
    private ReportingProAccountService reportingProAccountService;

    @Autowired
    private SessionModel sessionModel;

    @RequestMapping(value = "/{reportingProAccountId}", method = RequestMethod.GET, produces = "application/json")
    public ReportingProAccountWebModel getAccountById(@PathVariable int reportingProAccountId){
        return reportingProAccountService.getAccountById(reportingProAccountId,sessionModel.getUser());
    }
}
