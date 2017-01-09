package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.data.repos.AccountNetworkRepository;
import com.wahwahnetworks.platform.data.repos.AccountPublisherRepository;
import com.wahwahnetworks.platform.data.repos.AccountRepository;
import com.wahwahnetworks.platform.data.repos.DemandSourceRepository;
import com.wahwahnetworks.platform.lib.NameUtils;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.rabbitmq.NetworkRMQModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.models.web.create.NetworkCreateModel;
import com.wahwahnetworks.platform.models.web.create.PublisherCreateModel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 6/8/2014.
 * Migrated by Brian.Bober 1/20/2016 to Publisher/Network/Account hierarchy
 */

@Service
public class NetworkService
{
	private static final Logger log = Logger.getLogger(NetworkService.class);

	@Autowired
	private AccountNetworkRepository networkRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private ProductService productService;

	@Autowired
	private GenerationService generationService;

	@Autowired
	private AuditService auditService;

    @Autowired
    private DemandSourceRepository demandSourceRepository;

	@Autowired
	private DemandSourceService demandSourceService;

	@Autowired
	private AmqpTemplate amqpTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

	@Autowired
	private RevenueModelService revenueModelService;

    @Autowired
    private AccountPublisherRepository publisherRepository;

	@Transactional(readOnly = true)
	public List<NetworkWebModel> query(String query, UserModel userModel)
	{
		Iterable<AccountNetwork> accounts = networkRepository.findByNameStartsWith(query);

		List<NetworkWebModel> networkWebModelList = new ArrayList<>();

		for (AccountNetwork account : accounts)
		{
			if (userModel.hasRole(UserRoleType.INTERNAL_USER))
			{
				networkWebModelList.add(new NetworkInternalWebModel(account));
			}
			else
			{
				if (userModel.getAccountId() == account.getId())
				{
					networkWebModelList.add(new NetworkInternalWebModel(account));
				}
			}
		}

		return networkWebModelList;
	}

    @Transactional
	public void deleteNetwork(SessionModel sessionModel, UserModel userModel, NetworkWebModel networkModel) throws Exception
	{
		if (networkModel.getAccountType() != AccountType.NETWORK)
			return;

		// Archive in OpenX
		AccountNetwork accountNetwork = getNetworkEntityById(networkModel.getAccountId(), userModel);
		accountNetwork.setArchived(true); // Need to do this first before updating openx
        accountNetwork.setSinglePublisher(null);
		accountNetwork.setMarketplaceDemandSource(null);
		networkRepository.save(accountNetwork); // Need to do this first before updating openx

		Pageable pageable = new PageRequest(0, RedPandaConstants.MaximumPageSize);

		// Delete demand sources
		for (DemandSource source: demandSourceRepository.findByAccountOrderByName(accountNetwork))
		{
			demandSourceService.deleteDemandSource(sessionModel, source.getId());
		}

		accountNetwork.setSinglePublisher(null); // Must not be single publisher any longer b/c otherwise we can't delete pub
		accountRepository.save(accountNetwork);

        List<AccountPublisher> publisherList = publisherRepository.findByParentAccount(accountNetwork);

		for(AccountPublisher publisher : publisherList){
            publisherService.deletePublisher(publisher,userModel,false);
        }

		// Delete Revenue Model
		revenueModelService.deleteBillableEntityForNetwork(accountNetwork);

		// NEVER deactivate order 20068ad3-c001-fff1-8123-39da2e - it is used by regular publishers
		networkRepository.delete(accountNetwork);

		auditService.addAuditEntry(userModel, AuditActionTypeEnum.NETWORK_DELETE, "Successfully deleted account " + networkModel.getName() + " (Type: " + networkModel.getAccountType().toString().toLowerCase() +  ")", "");

	}

	@Transactional(readOnly = true)
	public NetworkWebModel getNetworkModelById(int networkId, UserModel user)
	{
		if (!user.hasRole(UserRoleType.INTERNAL_USER) && user.getAccountId() != networkId)
		{
			return null;
		}

		AccountNetwork account = getNetworkEntityById( networkId, user);

		if (user.hasRole(UserRoleType.INTERNAL_USER))
		{
			return new NetworkInternalWebModel(account);
		}
		else
		{
			return new NetworkWebModel(account);
		}
	}

	@Transactional(readOnly = true)
	public AccountNetwork getNetworkEntityById(int networkId, UserModel user)
	{
		if (!user.hasRole(UserRoleType.INTERNAL_USER) && user.getAccountId() != networkId)
		{
			return null;
		}

		return networkRepository.findOne(networkId);

	}

    @Transactional
	public NetworkWebModel updateNetwork(NetworkWebModel networkModel, UserModel user) throws Exception
	{

		AccountNetwork accountNetwork = networkRepository.findOne(networkModel.getAccountId());

		if (accountNetwork.getDefaultPublisherId() == null)
		{
			final PublisherWebModel newDefaultPublisher = publisherService.createDefaultPublisher(accountNetwork, user);
			accountNetwork.setDefaultPublisherId(newDefaultPublisher.getAccountId()); // To be safe, in case saving network could overwrite this value saved in createDefaultPublisher
		}
		else
		{
			// Rename default publisher
			log.info("Renaming default publisher");
			PublisherWebModel publisherWebModel = publisherService.getPublisherById(accountNetwork.getDefaultPublisherId(), user);
			String baseName = accountNetwork.getName();
			String defaultPublisherName = NameUtils.modifyNameWithPrependAndSuffix(baseName, 255,"Default publisher - ", "");
			publisherWebModel.setName(defaultPublisherName);
			publisherService.updatePublisher(publisherWebModel, user);
		}

		if (user.hasRole(UserRoleType.INTERNAL_USER) && networkModel instanceof NetworkInternalWebModel)
		{
			NetworkInternalWebModel internalWebModel = (NetworkInternalWebModel) networkModel;
			accountNetwork.setInternalNotes(internalWebModel.getInternalNotes());
		}

		accountNetwork.setAccountNotes(networkModel.getAccountNotes());
		accountNetwork.setName(networkModel.getName());
		accountNetwork.setCountry(networkModel.getCountry());
		accountNetwork.setLanguage(networkModel.getLanguage());
		accountNetwork.setContactName(networkModel.getContactName());
		accountNetwork.setContactEmail(networkModel.getContactEmail());
		accountNetwork.setPassbackDisplayTagHtml(networkModel.getPassbackDisplayTagHtml());

        if(networkModel.isSinglePublisher() && publisherService.getPublisherCountForAccountId(networkModel.getAccountId()) == 1)
		{
            AccountPublisher publisher = (AccountPublisher)accountRepository.findOne(networkModel.getSinglePublisherId());
            accountNetwork.setSinglePublisher(publisher);
        }
		else
		{
            accountNetwork.setSinglePublisher(null);
        }

		accountRepository.save(accountNetwork);

		auditService.addAuditEntry(user, AuditActionTypeEnum.NETWORK_SAVE, "Successfully saved account " + networkModel.getName() + " (Type: " + networkModel.getAccountType().toString().toLowerCase() +  ")", "");

		if(networkModel instanceof NetworkInternalWebModel){
			NetworkInternalWebModel networkInternalWebModel = (NetworkInternalWebModel)networkModel;
			accountNetwork.setInternalNotes(networkInternalWebModel.getInternalNotes());
		}

		// Now update OpenX

		String oxNotes = "Generated by Red Panda Platform.\n" + "RP network: " + accountNetwork.getName() + " \n\n";

		String baseName = accountNetwork.getName();

		if (user.hasRole(UserRoleType.INTERNAL_USER))
		{
			return new NetworkInternalWebModel(accountNetwork);
		}
		else
		{
			return new NetworkWebModel(accountNetwork);
		}
	}

	@Transactional
	public NetworkWebModel convertFreeAccountToNetwork(AccountFree freeAccount, UserModel userModel) throws Exception
	{
        jdbcTemplate.update("UPDATE accounts SET type = 'NETWORK' WHERE id = ?",freeAccount.getId());
        jdbcTemplate.update("DELETE FROM account_free WHERE account_id = ?",freeAccount.getId());
        jdbcTemplate.update("INSERT INTO account_networks (account_id) VALUES(?)",freeAccount.getId());

        entityManager.clear();

        AccountNetwork networkAccount = networkRepository.findOne(freeAccount.getId());

        initializeNetwork(userModel,networkAccount,true);

		networkRepository.save(networkAccount);

        Iterable<DemandSource> demandSources = demandSourceService.getDemandSourcesForAccount(networkAccount);

        return new NetworkWebModel(networkAccount);
	}


    @Transactional
	public NetworkWebModel createNetwork(NetworkCreateModel networkCreateModel, UserModel userModel) throws Exception
	{
		if(networkCreateModel.getAccountType() != AccountType.NETWORK)
		{
			throw new Exception("Unsupported Account Type. This API only supports Network account type");
		}

		String baseName = networkCreateModel.getName();

		NetworkInternalWebModel networkModel = new NetworkInternalWebModel();
		networkModel.setName(baseName);
		networkModel.setCountry(networkCreateModel.getCountry());
		networkModel.setLanguage(networkCreateModel.getLanguage());
		networkModel.setAccountType(networkCreateModel.getAccountType());

		if (accountRepository.findByName(networkModel.getName()) != null) // accountRepository intentional since many things won't like network/publisher name collisions
		{
			throw new Exception("Duplicate name error");
		}

		AccountNetwork account = new AccountNetwork();


		if (userModel.hasRole(UserRoleType.INTERNAL_USER))
		{
			account.setInternalNotes(networkModel.getInternalNotes());
		}

		//account.setAccountNotes(networkModel.getAccountNotes());
		account.setName(networkModel.getName());
		account.setCountry(networkModel.getCountry());
		account.setLanguage(networkModel.getLanguage());
		account.setContactName(networkModel.getContactName());
		account.setContactEmail(networkModel.getContactEmail());
		account.setDefaultProductFormat(ProductFormat.OUTSTREAM);

		networkRepository.save(account);

        initializeNetwork(userModel,account,networkCreateModel.getAutoCreateNetworkPublisher());

		if (userModel.hasRole(UserRoleType.INTERNAL_USER))
		{
			return new NetworkInternalWebModel(account);
		}
		else
		{
			return new NetworkWebModel(account);
		}

	}

    private void initializeNetwork(UserModel userModel, AccountNetwork account, boolean autoCreateNetworkPublisher) throws Exception {

        String baseName = account.getName();

        networkRepository.save(account);

        auditService.addAuditEntry(userModel, AuditActionTypeEnum.NETWORK_CREATE, "Successfully created account " + account.getName() + " (Type: " + account.getAccountType().toString().toLowerCase() +  ")", "");

        if (autoCreateNetworkPublisher)
        {

			PublisherCreateModel publisherCreateModel = new PublisherCreateModel();
			publisherCreateModel.setNetworkId(account.getId()); // This is actually done again in PublisherService
			publisherCreateModel.setName(baseName);
			publisherCreateModel.setAccountType(AccountType.PUBLISHER);
			publisherCreateModel.setCountry(account.getCountry());
			publisherCreateModel.setLanguage(account.getLanguage());
			publisherCreateModel.setAutoCreateSiteAndToolbar(false); // Note: Will still create default site
			publisherCreateModel.setSite(null); // Todo: For now
			publisherCreateModel.setAccountType(AccountType.PUBLISHER);

            // Network publisher
            final PublisherWebModel networkPublisher = publisherService.createPublisher(publisherCreateModel, userModel, false);

            AccountPublisher singlePublisher = (AccountPublisher)accountRepository.findOne(networkPublisher.getAccountId());
            account.setSinglePublisher(singlePublisher);
        }

		networkRepository.save(account);

        // Send Network Create
		NetworkRMQModel rmqModel = new NetworkRMQModel();
		rmqModel.setNetworkId(account.getId());
        amqpTemplate.convertAndSend("Network","Create",rmqModel);

    }

    @Transactional(readOnly = true)
	public NetworkWebListModel getNetworks(Pageable pageable)
	{
		Page<AccountNetwork> accounts =  networkRepository.findAll(pageable);
		return new NetworkWebListModel(accounts);
	}



	@Transactional
	public String grabUniversalNetworkTag(NetworkWebModel networkWebModel, UserModel user) throws Exception
	{

		AccountNetwork network = networkRepository.getOne(networkWebModel.getAccountId());
		Pageable pageable = new PageRequest(0, RedPandaConstants.MaximumPageSize);
		PublisherWebListModel pubList = publisherService.getForParentAccount((Account) network, pageable, false);
		// Todo: Do this asynchronously
		for (PublisherWebModel publisherWebModel: pubList.getPublishers())
		{
			// First publish the products
			ProductListModel productListForPublisher = productService.getProductListForPublisher(publisherWebModel.getAccountId(), user);
			List<ProductModel> products = productListForPublisher.getProducts();
			for (int i = 0; i < products.size(); i++)
			{
				ProductModel productModel = products.get(i);
				Product product = productService.getProductEntityById(productModel.getId(), user);
				if (!product.getLocked() && product.getWidgetId() == null) // Generation service checks if locked, but let's avoid unecessary warnings; only publish when locked
				{
					productService.requestPublishProduct(productModel.getId());
				}
			}
		}

		return generationService.generateUniversalNetworkTag(network);
	}

}
