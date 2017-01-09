package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.AdUnit;
import com.wahwahnetworks.platform.data.entities.LineItem;
import com.wahwahnetworks.platform.data.entities.Product;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.TagSettingsCreateModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 2/19/16.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/tag-settings")
public class TagSettingsController extends BaseAPIController
{

	private static final Logger log = Logger.getLogger(TagSettingsController.class);

	@Autowired
	private SiteService siteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private TargetingService targetingService;

	@Autowired
	private NetworkService networkService;

    @Autowired
    private ReportingProAccountService reportingProAccountService;

	@Autowired
	private DomainService domainService;

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private ClientFeatureService clientFeatureService;

	@RequestMapping(method = RequestMethod.GET, value = "site-{siteId}")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	@Transactional
	public TagSettingsModel getTagSettingsForSite(@PathVariable Integer siteId) throws Exception
	{

		TagSettingsModel tagSettingsModel = new TagSettingsModel();

		// Retrieve Information
		SiteModel siteModel = siteService.getSiteById(siteId, sessionModel.getUser());
		ProductListModel productListModel = productService.getProductListForSite(siteId, sessionModel.getUser());

		PublisherWebModel publisherWebModel = publisherService.getPublisherById(siteModel.getAccountId(), sessionModel.getUser());

		NetworkWebModel networkWebModel = null;
		ReportingProAccountWebModel reportingProAccountWebModel = null;

		if (publisherWebModel.getParentAccountId() != null)
		{
			if(publisherWebModel.getParentAccountType() == AccountType.NETWORK){
                networkWebModel = networkService.getNetworkModelById(publisherWebModel.getParentAccountId(), sessionModel.getUser());
			}

			if(publisherWebModel.getParentAccountType() == AccountType.REPORTING_PRO){
			    reportingProAccountWebModel = reportingProAccountService.getAccountById(publisherWebModel.getParentAccountId(), sessionModel.getUser());
            }
		}

		List<DomainManagementModel> domains = null; // Null is important, so in test environ w/o analytics running, domains don't get changed
		List<DomainManagementModel> suggestedDomains = null;
		try
		{
			domains = domainService.listDomainsForSite(sessionModel, siteId);
			suggestedDomains = domainService.listSuggestedDomains(sessionModel, siteId);
		}
		catch (Exception e)
		{
			log.warn("Error connectiong to Analytics domain service. Continuing.");
		}

		List<ClientFeatureWebModel> availableFeatures = clientFeatureService.listClientFeatures().getClientFeatures();

		List<TagSettingsProductModel> tags = new ArrayList<>();

		for (ProductModel productModel : productListModel.getProducts())
		{
			if (!productModel.isDefault()) // Don't display default products
			{
				TagSettingsProductModel tagSettingsProductModel = new TagSettingsProductModel();
				tagSettingsProductModel.setProductModel(productModel);

				AdUnitListModel adUnits = productService.getAdUnitsListModelByProduct(productModel, sessionModel.getUser());
				tagSettingsProductModel.setAdUnits(adUnits.getAdUnits());

				List<com.wahwahnetworks.platform.data.entities.LineItem> targetedLineItems = targetingService.getLineItemsForProductModel(productModel);
				List<LineItemModel> targetedLineItemModels = new ArrayList<LineItemModel>();
				for (LineItem lineItem: targetedLineItems)
				{
					targetedLineItemModels.add(new LineItemModel(lineItem));
				}

				tagSettingsProductModel.setLineItemsTargeted(targetedLineItemModels);

				tags.add(tagSettingsProductModel);

			}
		}

		tagSettingsModel.setSite(siteModel);
		tagSettingsModel.setTags(tags);

        if(networkWebModel != null){
            tagSettingsModel.setNetwork(networkWebModel);
        }

        if(reportingProAccountWebModel != null){
            tagSettingsModel.setNetwork(reportingProAccountWebModel);
        }

        tagSettingsModel.setDomains(domains);
		tagSettingsModel.setSuggestedDomains(suggestedDomains);
		tagSettingsModel.setAvailableFeatures(availableFeatures);
		tagSettingsModel.setParentDefaultProductFormat(publisherWebModel.getDefaultProductFormat());
		if (publisherWebModel.getDefaultProductFormat() == null)
			tagSettingsModel.setParentDefaultProductFormat(publisherWebModel.getParentDefaultProductFormat());

		return tagSettingsModel;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "site-{siteId}")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	@Transactional
	public TagSettingsModel updateTagSettingsForSite(@PathVariable Integer siteId, @RequestBody TagSettingsModel tagSettingsModel) throws Exception
	{

		// Get one tag's settings, since we have no other choice right now. We really should share some settings between tags PLATFORM-957 and PLATFORM-926
		TagSettingsProductModel firstNonDefaultTag = null;
		for (TagSettingsProductModel tagSettingsProductModel : tagSettingsModel.getTags())
		{
			if (!tagSettingsProductModel.getProductModel().isDefault())
			{
				firstNonDefaultTag = tagSettingsProductModel;
				break;
			}
		}

		siteService.updateSite(tagSettingsModel.getSite(), sessionModel.getUser(), firstNonDefaultTag);

		// Only copy site name to tag if there's one tag for this site. Else, UI needs to set it.
		if (tagSettingsModel.getTags().size() == 1)
		{
			tagSettingsModel.getTags().get(0).getProductModel().setName(tagSettingsModel.getSite().getSiteName());
		}


		for (TagSettingsProductModel tagSettingsProductModel : tagSettingsModel.getTags())
		{
			// This only happens for standard tags. See ProductService.updateDefaultTagsProduct for rest
			// Apply version # to standard tags. Todo - replace some of this w/ PLATFORM-957 and PLATFORM-926
			ProductModel currentProductModel = tagSettingsProductModel.getProductModel();
			ProductModel firstNonDefaultTagProductModel = firstNonDefaultTag.getProductModel();
			currentProductModel.setProductVersion(firstNonDefaultTagProductModel.getProductVersion());
			currentProductModel.setLogLevel(firstNonDefaultTagProductModel.getLogLevel());
			currentProductModel.setDebug(firstNonDefaultTagProductModel.getDebug());

			productService.updateProduct(tagSettingsProductModel.getProductModel(), sessionModel.getUser());

            for(AdUnitModel adUnitModel : tagSettingsProductModel.getAdUnits()){
                adUnitModel.setProductId(tagSettingsProductModel.getProductModel().getId());
            }

			List<AdUnit> adUnitList = productService.convertAdUnitListModelToAdUnitList(tagSettingsProductModel.getAdUnits());
			productService.updatePassbackAdUnits(adUnitList, tagSettingsProductModel.getProductModel(), sessionModel.getUser());
		}

		if (tagSettingsModel.getDomains() != null)
		{
			domainService.updateDomainsForSite(sessionModel, siteId, tagSettingsModel.getDomains());
		}

		return getTagSettingsForSite(siteId);
	}

	@RequestMapping(method = RequestMethod.POST, value = "site-{siteId}/tags")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	@Transactional
	public TagSettingsProductModel createTagForSite(@PathVariable Integer siteId, @RequestBody TagSettingsCreateModel createModel) throws Exception
	{
		Site site = siteService.getSiteEntityById(siteId, sessionModel.getUser());

		ProductModel productModel = productService.createAdditionalProduct(sessionModel.getUser(), site, createModel);

		TagSettingsProductModel tagSettingsProductModel = new TagSettingsProductModel();
		tagSettingsProductModel.setProductModel(productModel);

		AdUnitListModel adUnits = productService.getAdUnitsListModelByProduct(productModel, sessionModel.getUser());
		tagSettingsProductModel.setAdUnits(adUnits.getAdUnits());

		return tagSettingsProductModel;
	}


}
