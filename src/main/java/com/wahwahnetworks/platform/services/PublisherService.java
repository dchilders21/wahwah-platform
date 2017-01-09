package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.controllers.api.SiteController;
import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.*;
import com.wahwahnetworks.platform.data.repos.AccountNetworkRepository;
import com.wahwahnetworks.platform.data.repos.AccountPublisherRepository;
import com.wahwahnetworks.platform.data.repos.AccountRepository;
import com.wahwahnetworks.platform.data.repos.SiteRepository;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.lib.NameUtils;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.rabbitmq.PublisherRMQModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.models.web.create.PublisherCreateModel;
import com.wahwahnetworks.platform.models.web.create.SiteCreateModel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 6/8/2014.
 * Migrated by Brian.Bober 1/20/2016 to Publisher/Network/Account hierarchy
 */

@Service
public class PublisherService
{

	private static final Logger log = Logger.getLogger(SiteController.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountPublisherRepository publisherRepository;

	@Autowired
	private AccountNetworkRepository networkRepository;

	@Autowired
	private SiteService siteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private GenerationService generationService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private NetworkService networkService;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private RevenueModelService revenueModelService;

	@Autowired
	private LineItemService lineItemService;

    @Autowired
    private SiteRepository siteRepository;

	@Transactional
	public List<PublisherWebModel> query(String query, UserModel userModel)
	{
		Iterable<AccountPublisher> accounts = publisherRepository.findByNameStartsWithAndIsDefaultIsFalse(query);

		List<PublisherWebModel> publisherWebModelList = new ArrayList<>();

		for (AccountPublisher account : accounts)
		{
			if (userModel.hasRole(UserRoleType.INTERNAL_USER))
			{
				RevenueModel revenueModel = revenueModelService.getRevenueModelForPublisher(account);
				// Todo: Create new Repo function that returns only publishers instead of this slower check
				if (account.getAccountType() == AccountType.PUBLISHER)
					publisherWebModelList.add(new PublisherInternalWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId())));
			}
			else
			{
				if (userModel.getAccountId() == account.getId())
				{
					// Todo: Create new Repo function that returns only publishers instead of this slower check
					if (account.getAccountType() == AccountType.PUBLISHER)
						publisherWebModelList.add(new PublisherWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId())));
				}
			}
		}

		return publisherWebModelList;
	}

	@Transactional(readOnly = true)
	public AccountPublisher getParentRedPandaPublisherCreator(AccountPublisher publisher)
	{
		return publisherRepository.findByMarketplacePublisher(publisher);
	}

	@Transactional(readOnly = true)
	public int getPublisherCountForAccountId(Integer accountId)
	{
        Account account = null;

        if(accountId != null){
            account = accountRepository.findOne(accountId);
        }

        return publisherRepository.countByParentAccount(account);
	}

	@Transactional(readOnly = true)
	public PublisherWebListModel getForParentAccount(Account parentAccount, Pageable pageable, Boolean seeAll)
	{
		Page<AccountPublisher> publishers;
		if (seeAll == true)
			publishers = publisherRepository.findAll(pageable);
		else
			publishers = publisherRepository.findByParentAccount(parentAccount,pageable);
		return new PublisherWebListModel(publishers,null); // Todo: Can't be null since pub may be created by red panda pub
	}

	@Transactional(readOnly = true)
	public PublisherWebListModel getForParentAccountWithoutDefaults(Account parentAccount, Pageable pageable, Boolean seeAll)
	{
		Page<AccountPublisher> publishers;
		if (seeAll == true)
			publishers = publisherRepository.findByIsDefaultIsFalse(pageable);
		else
			publishers = publisherRepository.findByParentAccountAndIsDefaultIsFalse(parentAccount,pageable);
		return new PublisherWebListModel(publishers,null); // Todo: Can't be null since pub may be created by red panda pub
	}

    @Transactional
    public void deletePublisher(PublisherWebModel publisherWebModel, UserModel userModel, Boolean defaultEntryCheck) throws Exception
    {
        AccountPublisher publisher = publisherRepository.findOne(publisherWebModel.getAccountId());
        deletePublisher(publisher,userModel,defaultEntryCheck);
    }

    @Transactional
	public void deletePublisher(AccountPublisher accountPublisher, UserModel userModel, Boolean defaultEntryCheck) throws Exception
	{
        if (accountPublisher.isDefault() && defaultEntryCheck == true)
		{
			log.warn("Skipping delete of publisher "+accountPublisher.getId()+". Default publisher detected with defaultEntryCheck enabled. ");
			return;
		}

		if (accountPublisher.getParentAccount() != null)
		{
			AccountNetwork accountNetwork = (AccountNetwork)(accountPublisher.getParentAccount());
			if (accountNetwork.getSinglePublisher() != null)
			{
				throw new ModelValidationException("Cannot delete a single publisher from a single publisher network");
			}
		}

		// Send out archive message to listeners, mostly for ad server handlers, etc
		// Do this before any delete messages
		// !!! Note: Some values may be invalid by the time queue is processed (e.g. site may no longer exist in platform)
		// We intentional use internal web model here, since the queue is internal only, not going to ui through web

		PublisherRMQModel rmqPublisherModel = new PublisherRMQModel();
		rmqPublisherModel.setRedPandaName(accountPublisher.getName());
		rmqPublisherModel.setRedPandaId(accountPublisher.getId());
		amqpTemplate.convertAndSend("Publisher","Archive", rmqPublisherModel);

		// Archive in OpenX
		accountPublisher.setArchived(true); // Need to do this first before updating openx
		publisherRepository.save(accountPublisher); // Need to do this first before updating openx

		// First delete sites
        Iterable<Site> siteList = siteRepository.findByPublisher(accountPublisher);

        for(Site site : siteList){
            siteService.deleteSite(site,userModel,false);
        }

        // Delete Revenue Model
        revenueModelService.deleteBillableEntityForPublisher(accountPublisher);


		Integer marketplaceAccountId = null;

		if(accountPublisher.getMarketplacePublisher() != null){
			marketplaceAccountId = accountPublisher.getMarketplacePublisher().getId();
		}

		// Delete Targeting
		lineItemService.deleteTargetingForPublisher(accountPublisher);

		publisherRepository.delete(accountPublisher);

		// Delete after network publisher because of foreign key constraint
		if (marketplaceAccountId != null)
		{
            AccountPublisher marketplacePublisher = publisherRepository.findOne(marketplaceAccountId);
			deletePublisher(marketplacePublisher, userModel, defaultEntryCheck);
		}

		auditService.addAuditEntry(userModel, AuditActionTypeEnum.PUBLISHER_DELETE, "Successfully deleted account " + accountPublisher.getName() + " (Type: " + accountPublisher.getAccountType().toString().toLowerCase() +  ")", "");
	}


	public AccountPublisher getPublisherEntityById(int publisherId, UserModel user)
	{
        if ((user.getAccountType() != AccountType.NETWORK && user.getAccountType() != AccountType.ROOT && user.getAccountType() != AccountType.REPORTING_PRO) && publisherId != user.getAccountId())
        {
            throw new EntityNotPermittedException("Not allowed to access this Publisher");
        }

		AccountPublisher publisher = publisherRepository.findOne(publisherId);

		if (publisher.getAccountType() != AccountType.PUBLISHER)
		{
			return null;
		}

		return publisher;
	}

	public PublisherWebModel getPublisherById(int publisherId, UserModel user)
	{
		if ((user.getAccountType() != AccountType.NETWORK && user.getAccountType() != AccountType.ROOT && user.getAccountType() != AccountType.REPORTING_PRO) && publisherId != user.getAccountId())
		{
			throw new EntityNotPermittedException("Not allowed to access this Publisher");
		}

		AccountPublisher account = getPublisherEntityById(publisherId, user);

		if(user.getAccountId() != null && account.getParentAccount().getId() != user.getAccountId()){
			throw new EntityNotPermittedException("Not allowed to access this Publisher");
		}

		if (user.hasRole(UserRoleType.NETWORK_ADMIN))
		{
			return new PublisherInternalWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId())); // Todo: Can't be null since pub may be created by red panda pub
		}
		else
		{
			return new PublisherWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId()));
		}
	}

	public PublisherWebModel updatePublisher(PublisherWebModel publisherModel, UserModel user) throws Exception
	{

		if (!user.hasRole(UserRoleType.INTERNAL_USER) && user.getAccountId() != publisherModel.getAccountId())
		{
			return null;
		}

		AccountPublisher account = publisherRepository.findOne(publisherModel.getAccountId());

		if (account == null || account.getAccountType() != AccountType.PUBLISHER)
		{
			return null;
		}

		if (account.getParentAccount() != null
			&& account.getParentAccount().getAccountType() == AccountType.NETWORK )
		{
			AccountNetwork network = (AccountNetwork)account.getParentAccount();
			if (network.getDefaultPublisherId() == null)
			{
				final PublisherWebModel newDefaultPublisher = createDefaultPublisher(network, user);
				network.setDefaultPublisherId(newDefaultPublisher.getAccountId()); // To be safe, in case saving network could overwrite this value saved in createDefaultPublisher
			}
		}

		if (account.getDefaultSiteId() == null)
		{
			final SiteModel newDefaultModel = siteService.createDefaultTagsSite(account.getId(), user); // Lazy-create default site if it doesn't exist
			account.setDefaultSiteId(newDefaultModel.getId()); // Just to be safe. Publisher should already be saved but we may overwrite it
		}
		else
		{
			// Rename it only if pub renamed!
			if (!account.getName().equals(publisherModel.getName()))
			{
				log.info("Renaming default site");
				String defaultSiteName = NameUtils.modifyNameWithPrependAndSuffix(publisherModel.getName(), 255, "Default site - ", "");
				SiteModel defaultSite = siteService.getSiteById(account.getDefaultSiteId(), user);
				defaultSite.setSiteName(defaultSiteName);
				siteService.updateSite(defaultSite, user, null /* not needed for name changes */);
			}
		}

		if (user.hasRole(UserRoleType.INTERNAL_USER) && publisherModel instanceof PublisherInternalWebModel)
		{
			PublisherInternalWebModel internalWebModel = (PublisherInternalWebModel) publisherModel;
			account.setInternalNotes(internalWebModel.getInternalNotes());
		}

		String pName = publisherModel.getName().replaceAll(NameUtils.pSuffix, "");
		account.setAccountNotes(publisherModel.getAccountNotes());
		account.setName(pName + NameUtils.pSuffix);
		account.setCountry(publisherModel.getCountry());
		account.setLanguage(publisherModel.getLanguage());
		account.setContactName(publisherModel.getContactName());
		account.setContactEmail(publisherModel.getContactEmail());
		account.setPassbackDisplayTagHtml(publisherModel.getPassbackDisplayTagHtml());

		publisherRepository.save(account);

		auditService.addAuditEntry(user, AuditActionTypeEnum.PUBLISHER_SAVE, "Successfully saved saved account " + publisherModel.getName() + " (Type: " + publisherModel.getAccountType().toString().toLowerCase() +  ")", "");

		// Now update inherited sites
		Pageable pageable = new PageRequest(0, RedPandaConstants.MaximumPageSize);

		SiteListModel siteListModel = siteService.getSiteListForPublisher(user, account.getId(), pageable, null);
		List<SiteModel> sites = siteListModel.getSites();
		for (int i = 0; i < sites.size() ; i++)
		{
			SiteModel site = sites.get(i);
			if (site.isInheritPubDetails())
			{
				site.setSiteCountry(account.getCountry());
				site.setLanguage(account.getLanguage());
				site.setContactName(account.getContactName());
				site.setContactEmail(account.getContactEmail());
				siteService.updateSite(site, user, null /* not needed for country */);
			}
		}


		// If linked marketplace publisher, update that too
		if (account.getMarketplacePublisher() != null)
		{
			PublisherWebModel marketplacePublisherWebModel = getPublisherById(account.getMarketplacePublisher().getId(), user);
			String name = account.getName().replaceAll(NameUtils.pSuffix, "");
			marketplacePublisherWebModel.setName(NameUtils.modifyNameWithPrependAndSuffix(name, 255, "", NameUtils.mpSuffix));
			updatePublisher(marketplacePublisherWebModel, user);
		}

		// Liverail

		String notes = "Generated by Red Panda Platform.\n" + "RP publisher: " + publisherModel.getName() + "\n\n";
		PublisherRMQModel rmqPublisherModel = new PublisherRMQModel();
		rmqPublisherModel.setRedPandaName(account.getName());
		rmqPublisherModel.setRedPandaId(account.getId());
		rmqPublisherModel.setNotes(notes);
		amqpTemplate.convertAndSend("Publisher","Update", rmqPublisherModel);

		if (user.hasRole(UserRoleType.INTERNAL_USER))
		{
			return new PublisherInternalWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId()));
		}
		else
		{
			return new PublisherWebModel(account, getParentRedPandaPublisherCreator(account), siteService.getSiteCountForPublisherId(account.getId()));
		}





	}

	@Transactional
	public PublisherWebModel createPublisher(PublisherCreateModel publisherCreateModel, UserModel userModel, Boolean isDefault) throws Exception
	{

		Boolean shouldCreateRedPandaPublisher = (publisherCreateModel.getNetworkId() != null);
		AccountNetwork masterAccount = null;
		if (publisherCreateModel.getNetworkId() != null)
		{
			masterAccount = networkService.getNetworkEntityById(publisherCreateModel.getNetworkId(), userModel);

			if (!isDefault && masterAccount.getDefaultPublisherId() == null && masterAccount.getChildAccounts().size() > 0)
			{
				final PublisherWebModel newDefaultPublisher = createDefaultPublisher(masterAccount, userModel);
				masterAccount.setDefaultPublisherId(newDefaultPublisher.getAccountId()); // To be safe, in case saving network could overwrite this value saved in createDefaultPublisher
			}
		}

		log.info("Creating publisher");
		String baseName = publisherCreateModel.getName();

		if(publisherCreateModel.getAccountType() == AccountType.NETWORK && publisherCreateModel.getAutoCreateSiteAndToolbar()){
			throw new ModelValidationException("Networks cannot auto-create site and publisher");
		}

		if (publisherCreateModel.getAutoCreateSiteAndToolbar())
		{
			// validate url in advance so nothing is created for invalid url
			if (!siteService.validateURL(publisherCreateModel.getSite().getSiteUrl()))
			{
				log.error("Error creating site: invalid url " + publisherCreateModel.getSite().getSiteUrl());
				throw new ServiceException("Error creating site: invalid url " + publisherCreateModel.getSite().getSiteUrl());
			}
		}

		if(publisherCreateModel.getAccountType() != AccountType.PUBLISHER)
		{
			throw new Exception("Unsupported Account Type. This API only supports Publisher account type");
		}

		Boolean shouldAutoCreateMarketplaceSite = publisherCreateModel.getAutoCreateSiteAndToolbar();
		Boolean shouldAutoCreateRPSite = false;

		SiteCreateModel siteCreateModel = publisherCreateModel.getSite(); // could be null

		PublisherInternalWebModel marketplacePublisherModel = new PublisherInternalWebModel();
		marketplacePublisherModel.setName(baseName);
		marketplacePublisherModel.setCountry(publisherCreateModel.getCountry());
		marketplacePublisherModel.setLanguage(publisherCreateModel.getLanguage());
		marketplacePublisherModel.setAccountType(publisherCreateModel.getAccountType());
		marketplacePublisherModel.setParentAccountId(null);

		if (shouldAutoCreateMarketplaceSite)
			siteCreateModel.setSiteName(baseName); // only use baseName. post-pend comes later

		// Create marketplace publisher first
		if (shouldCreateRedPandaPublisher)
		{
			marketplacePublisherModel.setName(NameUtils.modifyNameWithPrependAndSuffix(baseName, 255, "", NameUtils.mpSuffix));
			shouldAutoCreateRPSite = shouldAutoCreateMarketplaceSite;
			shouldAutoCreateMarketplaceSite = false;
		}
		PublisherWebModel marketPlacePublisher = createPublisherHelper(marketplacePublisherModel, userModel, shouldAutoCreateMarketplaceSite, (shouldAutoCreateMarketplaceSite)?siteCreateModel:null, false, null, false, !shouldCreateRedPandaPublisher);

		// Create Red Panda publisher
		if (shouldCreateRedPandaPublisher)
		{
			PublisherInternalWebModel redPandaPublisherModel = new PublisherInternalWebModel();
			redPandaPublisherModel.setName(NameUtils.modifyNameWithPrependAndSuffix(baseName, 255, "", NameUtils.pSuffix));
			redPandaPublisherModel.setCountry(publisherCreateModel.getCountry());
			redPandaPublisherModel.setLanguage(publisherCreateModel.getLanguage());
			redPandaPublisherModel.setAccountType(publisherCreateModel.getAccountType());
			redPandaPublisherModel.setParentAccountId(publisherCreateModel.getNetworkId());
			redPandaPublisherModel.setMarketplacePublisherAccountId(marketPlacePublisher.getAccountId());
			PublisherWebModel redPandaPublisher = createPublisherHelper(redPandaPublisherModel, userModel, shouldAutoCreateRPSite, (shouldAutoCreateRPSite)?siteCreateModel:null, true, masterAccount, false, !shouldCreateRedPandaPublisher);
			return redPandaPublisher;
		}

		return marketPlacePublisher;
	}

	@Transactional
	protected PublisherWebModel createDefaultPublisher(AccountNetwork network, UserModel userModel) throws Exception
	{
		/* Create default publisher only if it isn't in single publisher mode, only on create so that people can change back into
				 * single publisher mode if they haven't created a publisher */
		if (network.getDefaultPublisherId() != null)
		{
			return getPublisherById(network.getDefaultPublisherId(), userModel);
		}
		String baseName = network.getName();
		log.info("Creating default publisher");
		PublisherCreateModel defaultPublisherCreateModel = new PublisherCreateModel();
		defaultPublisherCreateModel.setNetworkId(network.getId()); // This is actually done again in PublisherService
		defaultPublisherCreateModel.setAccountType(AccountType.PUBLISHER);
		defaultPublisherCreateModel.setCountry(network.getCountry());
		defaultPublisherCreateModel.setLanguage(network.getLanguage());
		defaultPublisherCreateModel.setAutoCreateSiteAndToolbar(false); // Note: Will still create default site
		defaultPublisherCreateModel.setSite(null); // Todo: For now
		defaultPublisherCreateModel.setAccountType(AccountType.PUBLISHER);

		String defaultPublisherName = NameUtils.modifyNameWithPrependAndSuffix(baseName, 255,"Default publisher - ", "");
		defaultPublisherCreateModel.setName(defaultPublisherName);
		PublisherWebModel publisherWebModel = createPublisher(defaultPublisherCreateModel, userModel, true);
		AccountPublisher publisher = publisherRepository.getOne(publisherWebModel.getAccountId());
		publisher.setDefault(true);
		publisherRepository.save(publisher);
		final PublisherWebModel defaultNetworkPublisher  = getPublisherById(publisher.getId(), userModel);

		network.setDefaultPublisherId(defaultPublisherCreateModel.getNetworkId());
		networkRepository.save(network);
		return defaultNetworkPublisher;
	}

	@Transactional
	protected PublisherWebModel createPublisherHelper(PublisherWebModel publisherModel, UserModel userModel, Boolean autoCreateSiteAndFloater, SiteCreateModel siteCreateModel, Boolean isRedPandaPublisher, AccountNetwork masterAccount, Boolean isDefault, Boolean isMarketplaceOnly) throws Exception
	{

		if (accountRepository.findByName(publisherModel.getName()) != null)  // accountRepository intentional since many things won't like network/publisher name collisions
		{
			throw new Exception("Duplicate name error");
		}

		AccountPublisher accountPublisher = new AccountPublisher();

        AccountPublisher marketplacePublisher = null;

        if(publisherModel.getMarketplacePublisherAccountId() != null){
            marketplacePublisher = publisherRepository.findOne(publisherModel.getMarketplacePublisherAccountId());
        }

		accountPublisher.setAccountNotes(publisherModel.getAccountNotes());
		accountPublisher.setName(publisherModel.getName());
		accountPublisher.setAdServerType(AdServerType.OPEN_X);
		accountPublisher.setCountry(publisherModel.getCountry());
		accountPublisher.setLanguage(publisherModel.getLanguage());
		accountPublisher.setContactName(publisherModel.getContactName());
		accountPublisher.setContactEmail(publisherModel.getContactEmail());
		accountPublisher.setMarketplacePublisher(marketplacePublisher);
		accountPublisher.setDefaultProductFormat(null);

		if (accountPublisher.getParentAccount() == null)
		{
			accountPublisher.setDefaultProductFormat(ProductFormat.OUTSTREAM);
		}

		accountPublisher.setParentAccount(null);
		publisherRepository.save(accountPublisher);

		publisherModel.setAccountId(accountPublisher.getId());

		if (userModel.hasRole(UserRoleType.NETWORK_ADMIN) && publisherModel instanceof PublisherInternalWebModel)
		{
			accountPublisher.setInternalNotes(((PublisherInternalWebModel)publisherModel).getInternalNotes());
		}

		if (publisherModel.getParentAccountId() != null)
		{
			accountPublisher.setParentAccount(accountRepository.findOne(publisherModel.getParentAccountId()));
		}

		publisherRepository.save(accountPublisher);



		if (userModel.hasRole(UserRoleType.INTERNAL_USER))
		{
			publisherModel = new PublisherInternalWebModel(accountPublisher, getParentRedPandaPublisherCreator(accountPublisher), siteService.getSiteCountForPublisherId(accountPublisher.getId()));
		}
		else
		{
			publisherModel = new PublisherWebModel(accountPublisher, getParentRedPandaPublisherCreator(accountPublisher), siteService.getSiteCountForPublisherId(accountPublisher.getId()));
		}

		auditService.addAuditEntry(userModel, AuditActionTypeEnum.PUBLISHER_CREATE, "Successfully created account " + publisherModel.getName() + " (Type: " + publisherModel.getAccountType().toString().toLowerCase() +  ")", "");

		// Always create default site when creating a publisher
		SiteCreateModel defaultSiteCreateModel;

		if (siteCreateModel != null)
		{
			defaultSiteCreateModel = new SiteCreateModel(siteCreateModel);
		}
		else
		{
			defaultSiteCreateModel = new SiteCreateModel();
			defaultSiteCreateModel.setSiteName(publisherModel.getName().toString());
			defaultSiteCreateModel.setCountry(accountPublisher.getCountry());
			defaultSiteCreateModel.setSiteUrl("http://example.com/"); // This is fine because the domain shouldn't be used for anything for default sites
			defaultSiteCreateModel.setLanguage(accountPublisher.getLanguage());
			defaultSiteCreateModel.setPublisherId(accountPublisher.getId());
		}


		String accountName = accountPublisher.getName();
		if (isMarketplaceOnly || isRedPandaPublisher)
		{
			// Create default site
			defaultSiteCreateModel.setPublisherId(accountPublisher.getId());
			SiteModel newDefaultModel = siteService.createDefaultTagsSite(accountPublisher.getId(), userModel);
			accountPublisher.setDefaultSiteId(newDefaultModel.getId()); // Just to be safe. Publisher should already be saved but we may overwrite it
			// If not marketplace only, save marketplace default to publisher
			if (!isMarketplaceOnly)
			{
				AccountPublisher accountMarketplace = getPublisherEntityById(accountPublisher.getMarketplacePublisher().getId(), userModel);
				accountMarketplace.setDefaultSiteId(newDefaultModel.getMarketplaceSiteId());
				publisherRepository.save(accountMarketplace);
			}
		}

		// Won't be called when creating network
		if (autoCreateSiteAndFloater == true)
		{

			// Ok, they want a site and floater as well. Do that as well.
			siteCreateModel.setPublisherId(accountPublisher.getId());
			SiteModel newModel = null;
			if (!isDefault)
			{
				newModel = siteService.createSiteWithDefaults(siteCreateModel, userModel, false);
			}

			try
			{
				siteService.getSiteEntityById(newModel.getId(), userModel);
			}
			catch (Exception e)
			{
				throw new ModelValidationException("Error mapping site: " + e.getMessage());
			}
		}

		if (userModel.hasRole(UserRoleType.INTERNAL_USER))
		{
			return new PublisherInternalWebModel(accountPublisher, getParentRedPandaPublisherCreator(accountPublisher), siteService.getSiteCountForPublisherId(accountPublisher.getId()));
		}
		else
		{
			return new PublisherWebModel(accountPublisher, getParentRedPandaPublisherCreator(accountPublisher), siteService.getSiteCountForPublisherId(accountPublisher.getId()));
		}
	}

	@Transactional
	public String grabUniversalPublisherTag(PublisherWebModel publisherWebModel, UserModel user) throws Exception
	{
		// First publish the products
		ProductListModel productListForPublisher = productService.getProductListForPublisher(publisherWebModel.getAccountId(), user);
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

		AccountPublisher publisher = publisherRepository.findOne(publisherWebModel.getAccountId());
		return generationService.generateUniversalPublisherTag(publisher);
	}
}
