package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.DemandSourceListFilterModel;
import com.wahwahnetworks.platform.models.DemandSourceRequestModel;
import com.wahwahnetworks.platform.models.DemandSourceWizardCreateModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.analytics.ValidateDemandSourceCredentialsRequestModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.DemandSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by jhaygood on 1/27/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/demand-sources/")
public class DemandSourceController extends BaseAPIController {

    @Autowired
    private DemandSourceService demandSourceService;

    @Autowired
    private SessionModel sessionModel;

    @RequestMapping(method = RequestMethod.GET)
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceListModel listDemandSources(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize) throws Exception {

        LocalDate startDate;
        LocalDate endDate;

        LocalDate today = LocalDate.now();

        if(today.getDayOfMonth() == 1){
            startDate = today.plusMonths(-1).withDayOfMonth(1);
        } else {
            startDate = today.withDayOfMonth(1);
        }

        endDate = LocalDate.now().plusDays(-1); // Assume through yesterday by default

        DemandSourceListFilterModel filterModel = new DemandSourceListFilterModel();
        filterModel.setFilterDimensions(new ArrayList<>());
        filterModel.setShowInactive(false);
        filterModel.setStartDate(startDate);
        filterModel.setEndDate(endDate);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        return demandSourceService.getDemandSourcesForAccount(sessionModel,pageable,filterModel);
    }

    @RequestMapping(method = RequestMethod.POST)
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceModel createDemandSource(@RequestBody DemandSourceModel demandSourceModel){
        return demandSourceService.createDemandSourceFromModel(demandSourceModel,sessionModel.getUser().getAccountId());
    }

    @RequestMapping(method = RequestMethod.POST, value = "with-connection")
    public DemandSourceModel createDemandSourceFromWizard(@RequestBody DemandSourceWizardCreateModel demandSourceWizardCreateModel) throws IOException {
        DemandSourceModel demandSourceModel = demandSourceService.createDemandSourceFromModel(demandSourceWizardCreateModel.getDemandSource(),sessionModel.getUser().getAccountId());
        demandSourceService.createDemandSourceConnection(demandSourceWizardCreateModel.getConnection(),demandSourceModel.getDemandSourceId());
        return demandSourceModel;
    }

    @RequestMapping(method = RequestMethod.GET, value = "{demandSourceId}")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceModel getDemandSource(@PathVariable int demandSourceId){
        return demandSourceService.getDemandSource(demandSourceId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{demandSourceId}")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDemandSource(@PathVariable int demandSourceId) throws Exception {
        demandSourceService.deleteDemandSource(sessionModel,demandSourceId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "request")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DemandSourceModel requestDemandSource(@RequestBody DemandSourceRequestModel demandSourceRequestModel){

        DemandSourceModel demandSourceModel = new DemandSourceModel();
        demandSourceModel.setName(demandSourceRequestModel.getName());

        demandSourceService.requestDemandSource(sessionModel.getUser(),demandSourceRequestModel);
        return demandSourceService.createDemandSourceFromModel(demandSourceModel,sessionModel.getUser().getAccountId());
    }

    @RequestMapping(method = RequestMethod.GET, value = "{demandSourceId}/connections")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceConnectionListModel listConnectionsForDemandSource(@PathVariable int demandSourceId, @RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize) throws IOException {
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        return demandSourceService.getConnectionsForDemandSource(demandSourceId,pageable);
    }

    @RequestMapping(method = RequestMethod.POST, value = "{demandSourceId}/connections")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceConnectionModel createDemandSourceConnection(@PathVariable int demandSourceId, @RequestBody DemandSourceConnectionModel demandSourceConnectionModel) throws IOException {
        return demandSourceService.createDemandSourceConnection(demandSourceConnectionModel,demandSourceId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{demandSourceId}/connections/{demandSourceConnectionId}")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceConnectionModel getDemandSourceConnection(@PathVariable int demandSourceId, @PathVariable int demandSourceConnectionId) throws IOException {
        return demandSourceService.getConnectionForDemandSource(demandSourceId,demandSourceConnectionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "connection-types")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public DemandSourceConnectionTypeListModel listConnectionTypes(){
        return demandSourceService.getConnectionTypes();
    }

    @RequestMapping(method = RequestMethod.GET, value = "popular")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public PopularDemandSourceListModel listPopularDemandSources() throws IOException {
        return demandSourceService.getPopularDemandSources();
    }

    @RequestMapping(method = RequestMethod.POST, value = "validate-credentials")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    public GenericWebModel<Boolean> validateCredentials(@RequestBody  ValidateDemandSourceCredentialsRequestModel validateDemandSourceCredentialsRequestModel) throws Exception {
        boolean isValid = demandSourceService.validateCredentials(sessionModel,validateDemandSourceCredentialsRequestModel);

        GenericWebModel<Boolean> booleanGenericWebModel = new GenericWebModel<>();
        booleanGenericWebModel.setModel(isValid);
        return booleanGenericWebModel;
    }
}
