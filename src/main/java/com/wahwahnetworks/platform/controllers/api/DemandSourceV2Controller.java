package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.LineItemTargeting;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.models.*;
import com.wahwahnetworks.platform.models.analytics.ValidateDemandSourceCredentialsRequestModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Brian.Bober on 4/14/2016.
 */

@RestController
@Scope("request")
@RequestMapping("/api/2.0/demand-sources/")
public class DemandSourceV2Controller extends BaseAPIController
{

	private static final Logger log = Logger.getLogger(DemandSourceV2Controller.class);

	@Autowired
	private DemandSourceService demandSourceService;

	@Autowired
	private CreativeService creativeService;

	@Autowired
	private LineItemService lineItemService;

	@Autowired
	private SessionModel sessionModel;

    @Autowired
    private TargetingService targetingService;

	@RequestMapping(method = RequestMethod.GET, value = "{demandSourceId}")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	@Transactional
	public DemandSourceManagementModel getDemandSource(@PathVariable int demandSourceId) throws IOException
	{
		DemandSourceManagementModel demandSourceManagementModel = new DemandSourceManagementModel();

		final DemandSourceModel demandSourceModel = demandSourceService.getDemandSource(demandSourceId);
		demandSourceManagementModel.setDemandSourceModel(demandSourceModel);

		demandSourceManagementModel.setAccountWebModel(demandSourceService.getAccountForDemandSource(demandSourceId));
		demandSourceManagementModel.setConnections(demandSourceService.getConnectionsForDemandSource(demandSourceId));
		demandSourceManagementModel.setLineItems(lineItemService.getLineItemsForDemandSourceId(demandSourceId));
        demandSourceManagementModel.setCreatives(creativeService.getCreativesForDemandSourceId(demandSourceId));
		demandSourceManagementModel.setPlacements(demandSourceService.getPlacementsForDemandSourceId(demandSourceId));

		// Targeting
		for (LineItemModel lineItem : demandSourceManagementModel.getLineItems())
		{

			if (lineItem.getTargeting() == null)
				lineItem.setTargeting(new ArrayList<>());
			final Collection<LineItemTargeting> targetingRulesForLineItem = lineItemService.getTargetingRuleModelsForLineItem(lineItem.getId());
			for (LineItemTargeting lineItemTargeting : targetingRulesForLineItem)
			{
				lineItem.getTargeting().add(lineItemService.lineItemTargetingModelFromEntity(lineItemTargeting));
			}
		}

		return demandSourceManagementModel;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "{demandSourceId}")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	@Transactional
	public DemandSourceManagementModel updateDemandSource(@PathVariable int demandSourceId, @RequestBody DemandSourceManagementModel demandSourceManagementModel) throws Exception {
		if(demandSourceId == demandSourceManagementModel.getDemandSourceModel().getDemandSourceId())
		{
			UserModel user = sessionModel.getUser();

			final DemandSourceModel demandSourceModel = demandSourceService.getDemandSource(demandSourceId);

			demandSourceService.updateDemandSource(demandSourceManagementModel.getDemandSourceModel(), user);


			Pageable pageable = new PageRequest(0, 1000000);
			List<DemandSourceConnectionModel> newDemandSourceConnections = demandSourceManagementModel.getConnections();
			List<DemandSourceConnectionModel> oldDemandSourceConnections = demandSourceService.getConnectionsForDemandSource(demandSourceId, pageable).getDemandSourceConnections();
			for (DemandSourceConnectionModel connection: newDemandSourceConnections)
			{
				if (connection.getId() != null)
				{

						demandSourceService.updateConnection(connection);
				}
				else
				{
					demandSourceService.createDemandSourceConnection(connection, demandSourceId);
				}
			}
			// Delete any removed connections
			for (DemandSourceConnectionModel oldConnection: oldDemandSourceConnections)
			{
				boolean found = false;

				for (DemandSourceConnectionModel newConnection: newDemandSourceConnections)
				{
					if (newConnection.getId().equals(oldConnection.getId()))
					{
						found = true;
					}
				}

				if (!found)
				{
					demandSourceService.deleteConnection(oldConnection);
				}
			}

			demandSourceService.updatePlacements(demandSourceManagementModel.getPlacements());

            Map<Integer,Integer> creativeIdMap = creativeService.saveCreatives(demandSourceModel,demandSourceManagementModel.getCreatives());

            for(LineItemModel lineItemModel : demandSourceManagementModel.getLineItems()){
                for(DemandSourceLineItemAdModel adModel : lineItemModel.getAds()){
                    adModel.setCreativeId(creativeIdMap.get(adModel.getCreativeId()));
                }
            }

            lineItemService.saveLineItems(demandSourceModel,demandSourceManagementModel.getLineItems());

			log.info("Saving demand source " + demandSourceId + " complete.");

			return getDemandSource(demandSourceId);
		}

		throw new EntityNotPermittedException("Demand Source ID doesn't match DemandSourceModel");
	}

	@RequestMapping(method = RequestMethod.GET, value = "connection-types")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	public DemandSourceConnectionTypeListModel listConnectionTypes(){
		return demandSourceService.getConnectionTypes();
	}

	@RequestMapping(method = RequestMethod.GET, value = "popular")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	public PopularDemandSourceListModel listPopularDemandSources() throws IOException
	{
		return demandSourceService.getPopularDemandSources();
	}


	@RequestMapping(method = RequestMethod.POST, value = "validate-credentials")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	public GenericWebModel<Boolean> validateCredentials(@RequestBody ValidateDemandSourceCredentialsRequestModel validateDemandSourceCredentialsRequestModel) throws Exception {
		boolean isValid = demandSourceService.validateCredentials(sessionModel,validateDemandSourceCredentialsRequestModel);

		GenericWebModel<Boolean> booleanGenericWebModel = new GenericWebModel<>();
		booleanGenericWebModel.setModel(isValid);
		return booleanGenericWebModel;
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


	@RequestMapping(method = RequestMethod.DELETE, value = "{demandSourceId}")
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDemandSource(@PathVariable int demandSourceId) throws Exception {
		demandSourceService.deleteDemandSource(sessionModel,demandSourceId);
	}

    @RequestMapping(method = RequestMethod.POST)
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
	public DemandSourceListModel listDemandSourcesWithFilters(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize, @RequestBody DemandSourceListFilterModel filterModel) throws Exception {
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        return demandSourceService.getDemandSourcesForAccount(sessionModel,pageable,filterModel);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "priorities/network")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setNetworkPriorities(@RequestBody List<DemandSourcePriorityModel> priorities){
        //demandSourcePriorityService.setNetworkPriorities(sessionModel.getUser(),priorities);
    }

    @RequestMapping(method = RequestMethod.POST, value = "priorities/network/reset")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetNetworkPriorities(){
        //demandSourcePriorityService.resetNetworkPriorities(sessionModel.getUser());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "priorities/publisher/{publisherName}")
    public void setPublisherPriorities(@PathVariable String publisherName, @RequestBody List<DemandSourcePriorityModel> priorities){
        //demandSourcePriorityService.setPublisherPriorities(sessionModel.getUser(),publisherName,priorities);
    }

    @RequestMapping(method = RequestMethod.POST, value = "priorities/publisher/{publisherName}/reset")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPublisherPriorities(@PathVariable String publisherName){
        //demandSourcePriorityService.resetPublisherPriorities(sessionModel.getUser(),publisherName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "targetable-entities")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
	public TargetableEntityModelList getTargetableEntities(){
        List<TargetableEntityModel> entityModels = targetingService.getTargetableEntities(sessionModel.getUser().getAccountId());
        return new TargetableEntityModelList(entityModels);
    }
}
