package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountReportingPro;

/**
 * Created by jhaygood on 7/14/16.
 */

@WebSafeModel
public class ReportingProAccountWebModel extends AccountWebModel {


    public ReportingProAccountWebModel(AccountReportingPro accountReportingPro){
        super(accountReportingPro);
    }
}
