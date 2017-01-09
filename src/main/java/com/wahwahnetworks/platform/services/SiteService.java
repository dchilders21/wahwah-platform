package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.*;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.lib.NameUtils;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.rabbitmq.SiteRMQModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.models.web.create.SiteCreateModel;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteService
{
	private static final Logger log = Logger.getLogger(SiteService.class);

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private GenerationService generationService;


	@Autowired
	private LineItemTargetingRepository lineItemTargetingRepository;

	@Autowired
	private LineItemRepository lineItemRepository;

	@Autowired
	private AccountPublisherRepository publisherRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private RevenueModelService revenueModelService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private LineItemService lineItemService;

	@Transactional
	public SiteModel createSiteWithDefaults(SiteCreateModel siteCreateModel, UserModel userModel, Boolean isDefault) throws Exception
	{
		if (checkDuplicate(siteCreateModel.getSiteName(), -1))
		{
			throw new ServiceException("Duplicate name error " + siteCreateModel.getSiteName() + " already exists in Red Panda Platform.");
		}

		String baseName = siteCreateModel.getSiteName();

		if (!validateURL(siteCreateModel.getSiteUrl()))
		{
			log.error("Error creating site: invalid url " + siteCreateModel.getSiteUrl());
			throw new ServiceException("Error creating site: invalid url " + siteCreateModel.getSiteUrl());
		}

		// TODO - make asynchronous

		AccountPublisher accountPublisher = publisherRepository.findOne(siteCreateModel.getPublisherId());

        boolean isNetworkSite = accountPublisher.getParentAccount() instanceof AccountNetwork;
        boolean isReportingProSite = accountPublisher.getParentAccount() instanceof AccountReportingPro;

		if (accountPublisher.getDefaultSiteId() == null && isDefault != true)
		{
		    if(!isReportingProSite) {
                final SiteModel newDefaultModel = createDefaultTagsSite(accountPublisher.getId(), userModel); // Lazy-create default site if it doesn't exist
                accountPublisher.setDefaultSiteId(newDefaultModel.getId()); // Just to be safe. Publisher should already be saved but we may overwrite it
            }
		}

		AccountPublisher accountRP;
		AccountPublisher accountMarketplace;
		Boolean isRPNetwork = (accountPublisher.getMarketplacePublisher() != null);
		AccountNetwork accountNetwork = null;

		if (isRPNetwork)
		{
			accountNetwork = (AccountNetwork) accountPublisher.getParentAccount();
			accountMarketplace = accountPublisher.getMarketplacePublisher();
			accountRP = accountPublisher;
		}
		else
		{
			accountMarketplace = accountPublisher;
			accountRP = null;
		}

		// Start with marketplace account
		SiteCreateModel marketplaceCreateModel = new SiteCreateModel(siteCreateModel);
		marketplaceCreateModel.setPublisherId(accountMarketplace.getId());
		marketplaceCreateModel.setSiteName(NameUtils.modifyNameWithPrependAndSuffix(baseName.replace(NameUtils.pSuffix, ""), 255, "", NameUtils.mpSuffix));
		SiteModel marketplaceModel = createSite(accountMarketplace, marketplaceCreateModel, userModel, accountNetwork, isDefault);
		SiteModel newModel = marketplaceModel;

		if (accountRP != null)
		{
			// Reassigning newModel intentionally for return (should be the one user was actually editing)
			siteCreateModel.setSiteName(baseName);
			newModel = createSite(accountRP, siteCreateModel, userModel, accountNetwork, isDefault);
		}

        Site rpSite = getSiteEntityById(newModel.getId(), userModel);

        ProductFormat format = null;

        if(siteCreateModel.isCustomSite()){
            format = ProductFormat.CUSTOM;
        }

		if(!isReportingProSite){
            Site marketplaceSite = getSiteEntityById(marketplaceModel.getId(), userModel);
            String productNameMP = marketplaceCreateModel.getSiteName().replace(NameUtils.pSuffix, "");

            if (!isDefault)
            {
                productService.createProductWithDefaults(marketplaceSite,productNameMP, userModel, isRPNetwork, true, format);
            }

            ProductModel productModelDefaultMarketplace = productService.createDefaultTagsProduct(marketplaceSite, userModel, isRPNetwork, true);
            Product productDefaultMarketplace = productRepository.findOne(productModelDefaultMarketplace.getId());
            marketplaceSite.setDefaultProduct(productDefaultMarketplace);
            siteRepository.save(marketplaceSite);

            if(accountRP != null){
                // Delete default products for Marketplace Site
                List<Product> productList = new ArrayList<>();
                productList.addAll(marketplaceSite.getProducts());

                if(!marketplaceSite.isDefault()) {
                    for (Product product : productList) {
                        if (product.isDefault()) {
                            marketplaceSite.getProducts().remove(product);
                            productRepository.delete(product);
                        }
                    }

                    Assert.isTrue(marketplaceSite.getProducts().size() == 1);

                    marketplaceSite.setDefaultProduct(marketplaceSite.getProducts().get(0));

                }

                siteRepository.save(marketplaceSite);

                // Update RP site to create link to marketplace site
                rpSite.setMarketplaceSite(marketplaceSite);
                siteRepository.save(rpSite);

            }
        }

		if (accountRP != null && !isReportingProSite)
		{

			String productName = siteCreateModel.getSiteName().replace(NameUtils.pSuffix, "");

			if (!isDefault)
			{
				productService.createProductWithDefaults(rpSite, productName, userModel, isRPNetwork, false, format);
			}

			ProductModel productModelDefaultRP = productService.createDefaultTagsProduct(rpSite, userModel, isRPNetwork, false);
			Product productDefaultRP = productRepository.findOne(productModelDefaultRP.getId());
            rpSite.setDefaultProduct(productDefaultRP);
			siteRepository.save(rpSite);

            lineItemService.ensureMarketplaceLineItemForSite(rpSite);
		}

        siteRepository.save(rpSite);

		return getSiteById(newModel.getId(), userModel); // Query again b/c it may have redPandaMarketplaceSiteId
	}


	@Transactional
	public SiteModel createDefaultTagsSite(Integer publisherId, UserModel userModel) throws Exception
	{

		// Use accountId for parameter to force a new sql query (ensures we don't create it twice from dirty states)
		AccountPublisher accountPublisher = publisherRepository.findOne(publisherId);

		if (accountPublisher.getDefaultSiteId() != null) // It exists, don't create it
		{
			return getSiteById(accountPublisher.getDefaultSiteId(), userModel);
		}

		log.info("Create default tags site for publisher " + accountPublisher.getName());

		SiteCreateModel defaultSiteCreateModel = new SiteCreateModel();
		String defaultSiteName = NameUtils.modifyNameWithPrependAndSuffix(accountPublisher.getName(), 255,"Default site - ", "");
		defaultSiteCreateModel.setSiteName(defaultSiteName);
		defaultSiteCreateModel.setCountry(accountPublisher.getCountry());
		defaultSiteCreateModel.setLanguage(accountPublisher.getLanguage());
		defaultSiteCreateModel.setSiteUrl("http://example.com/");
		defaultSiteCreateModel.setCustomSite(false);
		defaultSiteCreateModel.setPublisherId(accountPublisher.getId());

		SiteModel siteModel = createSiteWithDefaults(defaultSiteCreateModel, userModel, true);

		if (siteModel.getMarketplaceSiteId() != null)
		{
			Site mpSite = siteRepository.getOne(siteModel.getMarketplaceSiteId());
			AccountPublisher mpAccount = mpSite.getPublisher();
			mpAccount.setDefaultSiteId(mpSite.getId());
			publisherRepository.save(mpAccount);
		}

		Site site = siteRepository.getOne(siteModel.getId());
		site.setDefault(true);
		siteRepository.save(site);
		accountPublisher.setDefaultSiteId(site.getId());
		publisherRepository.save(accountPublisher);
		return getSiteById(site.getId(), userModel);
	}

	/* Make internal notes null if not super-user */
	@Transactional
	protected SiteModel createSite(AccountPublisher accountPublisher, SiteCreateModel siteCreateModel, UserModel userModel, AccountNetwork accountNetwork /* Can be null */, Boolean isDefault) throws Exception
	{

		if (checkDuplicate(siteCreateModel.getSiteName(), -1))
		{
			throw new ServiceException("Duplicate name error " + siteCreateModel.getSiteName() + " already exists in Red Panda Platform.");
		}

		Boolean isMarketplace = (accountPublisher.getParentAccount() == null);

		Site site = new Site();

		SiteInternalModel siteModel = new SiteInternalModel();
		siteModel.setSiteName(siteCreateModel.getSiteName());
		siteModel.setLanguage(siteCreateModel.getLanguage());
		siteModel.setSiteUrl(siteCreateModel.getSiteUrl());
		siteModel.setSiteCountry(Country.UNITED_STATES); // not nullable
		// Todo: User account name can be network?
		//if (accountName == null && userModel.getAccountName() != null)
		//	accountName = userModel.getAccountName();
		siteModel.setAccountName(accountPublisher.getName());

		site.setAdServerType(AdServerType.OPEN_X);

		if (!validateURL(siteModel.getSiteUrl()))
		{
			log.error("Error creating site: invalid url " + siteModel.getSiteUrl());
			throw new ServiceException("Error creating site: invalid url " + siteModel.getSiteUrl());
		}


		site.setPublisher(accountPublisher);
		site.setSiteName(siteModel.getSiteName());
		site.setSiteUrl(siteModel.getSiteUrl());
		site.setContactName(siteModel.getContactName());
		site.setContactEmail(siteModel.getContactEmail());
		site.setLanguage(siteModel.getLanguage());
		site.setSiteCountry(siteModel.getSiteCountry());
		site.setTrafficEstimate(siteModel.getTrafficEstimate());
		site.setSiteType(siteModel.getSiteType());
		site.setSiteNotes(siteModel.getSiteNotes());
		site.setInheritPubDetails(true);
		site.setContactName(accountPublisher.getContactName());
		site.setContactEmail(accountPublisher.getContactEmail());
		site.setSiteCountry(accountPublisher.getCountry());
		site.setLanguage(accountPublisher.getLanguage());
		site.setDefault(isDefault);
		site.setDefaultProductFormat(null);

		if (siteModel instanceof SiteInternalModel)
		{
			SiteInternalModel siteInternalModel = siteModel;
			site.setInternalNotes(siteInternalModel.getInternalNotes());
		}

		siteRepository.save(site);

		auditService.addAuditEntry(userModel, AuditActionTypeEnum.SITE_CREATE, "Successfully created site " + siteModel.getSiteName() + " for publisher " + accountPublisher.getName(), "");
		siteModel.setId(site.getId());

		return siteModel;


	}

	private void setMarketplaceDefaultProduct(Site marketplaceSite){
		// Delete default products
		List<Product> productList = new ArrayList<>();
		productList.addAll(marketplaceSite.getProducts());

		for(Product product : productList){
			if(product.isDefault()){
				marketplaceSite.getProducts().remove(product);
				productRepository.delete(product);
			}
		}

		Assert.isTrue(marketplaceSite.getProducts().size() == 1);

		marketplaceSite.setDefaultProduct(marketplaceSite.getProducts().get(0));
	}

	/* Make internal notes null if not super-user. Ignore if null so not erasing  */
	@Transactional
	public SiteModel updateSite(SiteModel siteModel, UserModel userModel, TagSettingsProductModel firstNonDefaultTag  /* Can be null */) throws Exception
	{
		if (checkDuplicate(siteModel.getSiteName(), siteModel.getId()))
		{
			throw new ServiceException("Duplicate name error " + siteModel.getSiteName() + " already exists in Red Panda Platform.");
		}

		if (!validateURL(siteModel.getSiteUrl()))
		{
			log.error("Error creating site: invalid url " + siteModel.getSiteUrl());
			throw new ServiceException("Error updating site: invalid url " + siteModel.getSiteUrl());
		}

		boolean isReportingProAccount = false;

		Site site;
		AccountPublisher publisher = null;
		try
		{
			site = siteRepository.findOne(siteModel.getId());

			Account account;

			if (userModel.getAccountId() != null)
			{
				account = accountRepository.findOne(userModel.getAccountId());
			}
			else
			{
				account = site.getPublisher();
			}

			if (account instanceof AccountPublisher)
			{
				publisher = (AccountPublisher) account;

                isReportingProAccount = publisher.getParentAccount() instanceof AccountReportingPro;

				if (publisher.getDefaultSiteId() == null && !isReportingProAccount)
				{
					final SiteModel newDefaultModel = createDefaultTagsSite(publisher.getId(), userModel); // Lazy-create default site if it doesn't exist
					publisher.setDefaultSiteId(newDefaultModel.getId()); // Just to be safe. Publisher should already be saved but we may overwrite it
				}

				if (userModel.getAccountId() != null &&
						(account.getId() != userModel.getAccountId() || !userModel.hasRole(UserRoleType.PUBLISHER_ADMIN)))
				{
					throw new EntityNotPermittedException("You do have permission to access this site");
				}
			}

			if (account == null || account instanceof AccountNetwork)
			{
				SiteInternalModel siteInternalModel = (SiteInternalModel) siteModel;
				publisher = publisherRepository.findByName(siteInternalModel.getAccountName());
			}
		}
		catch (Exception exc)
		{
			log.error("Error updating site", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (site == null || publisher == null)
		{
			throw new EntityNotFoundException(String.format(
					"Site (id %d) or publisher (id %d) not found, cannot update site", siteModel.getId(),
					userModel.getAccountId()));
		}

		site.setPublisher(publisher);
		site.setSiteName(siteModel.getSiteName());
		site.setSiteUrl(siteModel.getSiteUrl());
		site.setContactName(siteModel.getContactName());
		site.setContactEmail(siteModel.getContactEmail());
		site.setLanguage(siteModel.getLanguage());
		site.setSiteType(siteModel.getSiteType());
		site.setSiteCountry(siteModel.getSiteCountry());
		site.setTrafficEstimate(siteModel.getTrafficEstimate());
		site.setSiteNotes(siteModel.getSiteNotes());
		site.setInheritPubDetails(siteModel.isInheritPubDetails());
		site.setDefaultProductFormat(siteModel.getDefaultProductFormat());
		site.setPassbackDisplayTagHtml(siteModel.getPassbackDisplayTagHtml());


		if (site.isInheritPubDetails())
		{
			site.setContactName(publisher.getContactName());
			site.setContactEmail(publisher.getContactEmail());
			site.setSiteCountry(publisher.getCountry());
			site.setLanguage(publisher.getLanguage());
		}

		if (siteModel instanceof SiteInternalModel)
		{
			SiteInternalModel siteInternalModel = (SiteInternalModel) siteModel;
			site.setInternalNotes(siteInternalModel.getInternalNotes());
		}

		siteRepository.save(site);

		boolean isMarketplace = (publisher.getParentAccount() == null);
		boolean isRPNetwork = (publisher.getMarketplacePublisher() != null);

        if(isMarketplace){
            Site networkSiteForMarketplaceSite = siteRepository.findByMarketplaceSite(site);

            if(networkSiteForMarketplaceSite != null){
                setMarketplaceDefaultProduct(site);
                lineItemService.ensureMarketplaceLineItemForSite(networkSiteForMarketplaceSite);
            }
        } else {
            if(site.getMarketplaceSite() != null && !isReportingProAccount){
            	setMarketplaceDefaultProduct(site.getMarketplaceSite());
                lineItemService.ensureMarketplaceLineItemForSite(site);
            }
        }

        if(!isReportingProAccount) {
            if (site.getDefaultProduct() == null) {
                productService.createDefaultTagsProduct(site, userModel, isRPNetwork, isMarketplace);
            } else {
                productService.updateDefaultTagsProduct(site, firstNonDefaultTag /* Can be null */);
            }
        }


		auditService.addAuditEntry(userModel, AuditActionTypeEnum.SITE_SAVE, "Successfully saved site " + siteModel.getSiteName() + " for publisher " + publisher.getName(), "");

		// Update marketplace site too!
		if (site.getMarketplaceSite() != null)
		{
			try
			{
				SiteModel marketplaceSiteModel = getSiteById(site.getMarketplaceSite().getId(), userModel);
				String name = site.getSiteName();
				marketplaceSiteModel.setSiteName(NameUtils.modifyNameWithPrependAndSuffix(name, 255, "", NameUtils.mpSuffix));
				marketplaceSiteModel.setSiteUrl(site.getSiteUrl());
				updateSite(marketplaceSiteModel, userModel, null /* Marketplace as part of a network will not have default products soon */);
			}
			catch(Exception e)
			{
				log.warn("Unexpected error in updateSite getSiteById");
			}
		}

		SiteRMQModel rmqSiteModel = new SiteRMQModel();
		rmqSiteModel.setRedPandaName(site.getSiteName());
		rmqSiteModel.setRedPandaId(site.getId());
		rmqSiteModel.setNotes("");

		amqpTemplate.convertAndSend("Site","Update", rmqSiteModel);

		return siteModel;
	}

	@Transactional(readOnly = true)
	public int getSiteCountForAccountId(Integer accountId)
	{
        Account account = null;

        if(accountId != null){
            account = accountRepository.findOne(accountId);
        }

		return siteRepository.countByPublisherParentAccountAndIsArchivedIsFalse(account);
	}

	@Transactional(readOnly = true)
	public int getSiteCountForPublisherId(Integer publisherId)
	{
		AccountPublisher accountPublisher = null;

		if(publisherId != null){
            accountPublisher = publisherRepository.findOne(publisherId);
		}

		return siteRepository.countByPublisherAndIsArchivedFalse(accountPublisher);
	}


	//  Note: This is not a Liverail site list
	@Transactional(readOnly = true)
	public SiteListModel getSiteList(UserModel userModel, Pageable pageable, SiteModel highlightSiteModel, Boolean seeAll)
	{
		Page<Site> sites;

		if (seeAll == true)
		{
			// Todo: Ensure proper permissions for this
			sites = siteRepository.findAll(pageable);
		}
		else
		{
			if (userModel.getAccountType() == AccountType.NETWORK || userModel.getAccountType() == AccountType.ROOT)
			{
				// Find By Network
				sites = siteRepository.findByPublisherParentAccountIdAndIsArchivedIsFalseAndIsDefaultIsFalseOrderBySiteNameAsc(userModel.getAccountId(), pageable);
			}
			else
			{
				// Find By Publisher
				sites = siteRepository.findByPublisherIdAndIsArchivedIsFalseAndIsDefaultIsFalseOrderBySiteNameAsc(userModel.getAccountId(), pageable);
			}
		}

		return getSiteListModelHelper(sites, userModel, highlightSiteModel);
	}

	// Note: This is not a Liverail site list
	@Transactional(readOnly = true)
	public SiteListModel getSiteListForPublisher(UserModel userModel, int publisherId, Pageable pageable, SiteModel highlightSiteModel)
	{

		if (!userModel.hasRole(UserRoleType.INTERNAL_USER))
		{
			Account account = accountRepository.findOne(userModel.getAccountId());

			if (account instanceof AccountPublisher)
			{
				if (account.getId() != publisherId)
				{
					throw new EntityNotPermittedException("User does not have access to publisher " + publisherId);
				}
			}

			if (account instanceof AccountNetwork)
			{
				AccountPublisher publisher = publisherRepository.findOne(publisherId);

				if (publisher.getParentAccount() != account)
				{
					throw new EntityNotPermittedException("User does not have access to publisher " + publisherId);
				}
			}

		}

		Page<Site> sites = siteRepository.findByPublisherIdAndIsArchivedIsFalseAndIsDefaultIsFalseOrderBySiteNameAsc(publisherId, pageable);

		return getSiteListModelHelper(sites, userModel, highlightSiteModel);
	}


	protected SiteListModel getSiteListModelHelper(Page<Site> sites, UserModel userModel, SiteModel highlightSiteModel)
	{
		if (sites != null)
		{
			// Non super-user shouldn't see internal notes, even through a tool like Fiddler or Charles. That could be VERY bad
			SiteListModel slm = new SiteListModel(sites, userModel.hasRole(UserRoleType.INTERNAL_USER));
			if (highlightSiteModel != null)
			{
				// See if highlightSiteId is in this list. It's very likely it's not
				List<SiteModel> siteList = slm.getSites();
				int index = siteList.indexOf(highlightSiteModel);
				if (index != -1)
				{
					slm.setHighlightId(highlightSiteModel.getId());
					slm.setHighlightOffset(index);
				}
			}

			for (SiteModel siteModel: slm.getSites())
			{
				try
				{
					Site s = getSiteEntityById(siteModel.getId(), userModel);
					if (s != null)
					{
						Site creator = getRedPandaSiteCreator(s);
						if (creator != null)
						{
							siteModel.setRedPandaSiteCreatorId(creator.getId());
						}
					}
				}
				catch (Exception e){
					log.warn("Unknown error in SiteService.SiteListModel. Shouldn't happen!");
				}
			}
			return slm;
		}
		else
		{
			throw new EntityNotFoundException("Sites list for user id " + userModel.getUserId() + " not found");
		}
	}

	@Transactional(readOnly = true)
	public Site getSiteEntityById(int siteId, UserModel userModel) throws Exception
	{
		Site site;

		try
		{
			site = siteRepository.findOne(siteId);
			site.setProductsIterable(productRepository.findBySiteId(siteId));

			if(userModel.getAccountType() == AccountType.NETWORK){

                boolean isNetworkSite = false;
                boolean isMarketplaceSiteForNetwork = false;

				if(site.getPublisher().getParentAccount() != null && site.getPublisher().getParentAccount().getId() == userModel.getAccountId()){
					isNetworkSite = true;
				}

                if(!isNetworkSite && site.getPublisher().getParentAccount() == null){
                    AccountPublisher networkPublisher = publisherService.getParentRedPandaPublisherCreator(site.getPublisher());

                    if(networkPublisher.getParentAccount().getId() == userModel.getAccountId()){
                        isMarketplaceSiteForNetwork = true;
                    }
                }

                if(!isNetworkSite && !isMarketplaceSiteForNetwork){
                    throw new EntityNotPermittedException("You do have permission to access this site");
                }
			}

			if(userModel.getAccountType() == AccountType.PUBLISHER){
				if(site.getPublisher().getId() != userModel.getAccountId()){
					throw new EntityNotPermittedException("You do have permission to access this site");
				}
			}
		}
		catch (Exception exc)
		{
			log.error("Error getting site by id", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (site != null)
		{
			return site;
		}
		else
		{
			throw new EntityNotFoundException("Site with id " + siteId + " not found");
		}
	}

	@Transactional(readOnly = true)
	public SiteModel getSiteById(int siteId, UserModel userModel) throws Exception
	{
		Site site;

		try
		{
			site = getSiteEntityById(siteId,userModel);
		}
		catch (Exception exc)
		{
			log.error("Error getting site by id", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (site != null)
		{
			return getWebModelForEntity(userModel, site);
		}
		else
		{
			throw new EntityNotFoundException("Site with id " + siteId + " not found");
		}
	}

	public SiteModel getWebModelForEntity(UserModel user, Site site) throws Exception
	{
		SiteModel siteModel;

		if (user.getAccountType() == AccountType.NETWORK || user.getAccountType() == AccountType.ROOT)
		{
			SiteInternalModel internalSiteModel = new SiteInternalModel(site);
			siteModel = internalSiteModel;
		}
		else
			siteModel = new SiteModel(site);


		Site creator = getRedPandaSiteCreator(site);
		if (creator != null)
		{
			siteModel.setRedPandaSiteCreatorId(creator.getId());
		}

		return siteModel;
	}


    @Transactional
    public void deleteSite(UserModel user, SiteModel siteModel, Boolean defaultEntryCheck) throws Exception
    {
        Site site = getSiteEntityById(siteModel.getId(), user);
        deleteSite(site,user,defaultEntryCheck);
    }


	@Transactional
	public void deleteSite(Site site, UserModel user, Boolean defaultEntryCheck) throws Exception
	{


		if (site.isDefault() && defaultEntryCheck == true)
		{
			log.warn("Skipping delete of site "+site.getId()+". Default site detected with defaultEntryCheck enabled. ");
			return;
		}

		// Send out message to listeners, mostly for ad server handlers, etc
		// !!! Note: Some values may be invalid by the time queue is processed (e.g. site may no longer exist in platform)
		SiteRMQModel rmqSiteModel = new SiteRMQModel();
		rmqSiteModel.setRedPandaName(site.getSiteName());
		rmqSiteModel.setRedPandaId(site.getId());
		amqpTemplate.convertAndSend("Site","Archive", rmqSiteModel);

		lineItemService.deleteTargetingForSite(site);

		// Line items and ad - fixd . Use deleteLineItem
		List<LineItem> lineItems = lineItemRepository.findAllBySite(site);
		for (LineItem lineItem: lineItems)
		{
			lineItemService.deleteLineItem(lineItem);
		}

		Site marketplaceSite = site.getMarketplaceSite();

		// Delete products first due to constraint
		for (Product product: site.getProducts())
		{
			productService.deleteProductById(product.getId(), user, false);
		}

		site.getPublisher().getSites().remove(site); // prevent stale row count error
		publisherRepository.save(site.getPublisher()); // prevent stale row count cont'd

        revenueModelService.deleteBillableEntityForSite(site);

		siteRepository.delete(site);

		// Delete after network site because of foreign key constraint
		if (marketplaceSite != null)
		{
			deleteSite(user, new SiteModel(marketplaceSite), defaultEntryCheck);
		}

		auditService.addAuditEntry(user, AuditActionTypeEnum.SITE_DELETE, "Successfully deleted site " + site.getSiteName(), "");
	}

	public Boolean validateURL(String url)
	{
		String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (urlValidator.isValid(url))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Site getRedPandaSiteCreator(Site site)
	{
		return siteRepository.findByMarketplaceSite(site);
	}

	// Check if duplicate site exists
	// We have to check globally for duplicate sites, not just in the account.
	// Due to analytics limitation (or else we have to mangle site name to add account id
	// https://wahwah.atlassian.net/browse/PLATFORM-741
	private boolean checkDuplicate(/*Account account, */ String siteName, int siteId)
	{
		Iterable<Site> sites = siteRepository.findBySiteName(siteName);
		for (Site s : sites)
		{
			if (s.getSiteName().equals(siteName)
					&& s.getId() != siteId) // Make sure they aren't the same site
			{
				return true;
			}
		}
		return false;
	}

	@Transactional
	protected List<Integer> getProductIdListForTag(List<Integer> siteIds, UserModel userModel) throws Exception
	{
		List<Integer> productIdList = new ArrayList<Integer>();
		for (int i = 0; i < siteIds.size(); i++)
		{
			int siteId = siteIds.get(i);
			Site site = getSiteEntityById(siteId, userModel);
			if (site != null && !site.isArchived()) // Don't grab tags from archived sites
			{
				List<Product> productList = site.getProducts();
				for (int j = 0; j < productList.size(); j++)
				{
					productIdList.add(productList.get(j).getId());
				}
			}
		}
		return productIdList;
	}

	public String grabTags(HttpServletResponse response, List<Integer> siteIds, UserModel userModel, String tagFileName) throws Exception
	{

		List<Integer> productIdList = getProductIdListForTag(siteIds, userModel);

		ProductTagListModel tags = null;
		try
		{
			tags = productService.grabTags(productIdList, userModel);
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}


		response.setContentType("text/plain");
		response.addHeader("Content-Disposition", "attachment; filename=\""+tagFileName+".txt"+"\"");

		return productService.createTagTextResponse(tags);
	}

	public String grabTagsZip(HttpServletResponse response, List<Integer> siteIds, UserModel userModel, String tagFileName) throws Exception
	{

		List<Integer> productIdList = getProductIdListForTag(siteIds, userModel);

		ProductTagListModel tags = null;
		try
		{
			tags = productService.grabTags(productIdList, userModel);
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment; filename=\""+tagFileName+".zip"+"\"");
		response.addHeader("Content-Transfer-Encoding", "binary");

		try
		{
			productService.createTagZipResponse(tags, response);
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Unknown error creating zip.");
		}

		return "";
	}



	@Transactional
	public String grabUniversalSiteTag(SiteModel siteModel, UserModel user) throws Exception
	{
		// First publish the products
		ProductListModel productListForPublisher = productService.getProductListForSite(siteModel.getId(), user);
		List<ProductModel> products = productListForPublisher.getProducts();
		// Todo: Do this asynchronously
		for (int i = 0; i < products.size(); i++)
		{
			ProductModel productModel = products.get(i);
			Product product = productService.getProductEntityById(productModel.getId(), user);
			if (!product.getLocked() && product.getWidgetId() == null) // Generation service checks if locked, but let's avoid unecessary warnings; only publish when locked
			{
				productService.requestPublishProduct(productModel.getId());
			}
		}

		Site site = siteRepository.findOne(siteModel.getId());
		return generationService.generateUniversalSiteTag(site);
	}
}
