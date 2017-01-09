package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.ProductType;
import com.wahwahnetworks.platform.data.entities.enums.StandaloneAdFormat;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.NetworkWebListModel;
import com.wahwahnetworks.platform.models.web.NetworkWebModel;
import com.wahwahnetworks.platform.models.web.create.NetworkCreateModel;
import com.wahwahnetworks.platform.services.NetworkService;
import com.wahwahnetworks.platform.services.ProductService;
import com.wahwahnetworks.platform.services.PublisherService;
import com.wahwahnetworks.platform.services.SiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Brian.Bober on 1/19/2016.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/networks")
public class NetworkController extends BaseAPIController
{

	private static final Logger log = Logger.getLogger(SiteController.class);

	@Autowired
	private NetworkService networkService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private SessionModel sessionModel;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@HasUserRole(UserRoleType.INTERNAL_USER)
	// INTERNAL_USER, not PUBLISHER_ADMIN, because PUBLISHER_ADMIN is still associated with a publisher
	public NetworkWebModel createNetwork(@Valid @RequestBody NetworkCreateModel networkCreateModel, BindingResult result)
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}


		if (networkCreateModel.getName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Network is over 255 characters.");
		}

		NetworkWebModel networkWebModel;
		try
		{
			networkWebModel = networkService.createNetwork(networkCreateModel, sessionModel.getUser());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new ModelValidationException(e.getMessage());
		}

		return networkWebModel;

		// No auto-create site and floater since networks should have multiple marketplace sites, not one
	}


	@RequestMapping(value = "/query/{query}", method = RequestMethod.GET, produces = "application/json")
	public NetworkWebListModel search(@PathVariable String query)
	{
		List<NetworkWebModel> networkWebListModel = networkService.query(query, sessionModel.getUser());
		return new NetworkWebListModel(networkWebListModel);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public NetworkWebListModel listNetworks(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize)
	{
		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		NetworkWebListModel networkWebListModel = networkService.getNetworks(pageable);
		return networkWebListModel;
	}


	@RequestMapping(value = "/{networkId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@HasUserRole(UserRoleType.INTERNAL_USER)
	public NetworkWebModel updateNetwork(@Valid @RequestBody NetworkWebModel networkModel, BindingResult result, @PathVariable Integer networkId)
	{

		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}


		if (networkModel.getName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Network name is over 255 characters.");
		}


		NetworkWebModel networkWebModel;
		try
		{
			networkWebModel = networkService.updateNetwork(networkModel, sessionModel.getUser());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ModelValidationException(e.getMessage());
		}

		return networkWebModel;
	}

	@RequestMapping(value = "/{networkId}", method = RequestMethod.GET, produces = "application/json")
	public NetworkWebModel getNetworkById(@PathVariable Integer networkId) throws Exception
	{
		return networkService.getNetworkModelById(networkId, sessionModel.getUser());
	}

	@RequestMapping(value = "/{networkId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.INTERNAL_USER)
	// Regular user should not delete his own company's account. Also, require elevated WW user
	public void deleteNetwork(@PathVariable("networkId") int networkId) throws Exception
	{
		NetworkWebModel networkWebModel = networkService.getNetworkModelById(networkId, sessionModel.getUser());

		if (networkWebModel == null)
		{
			throw new EntityNotFoundException("Publisher with ID " + networkId + " does not exist");
		}

		networkService.deleteNetwork(sessionModel, sessionModel.getUser(), networkWebModel);
	}

	protected String getTagFileName( AccountNetwork network, UserModel userModel) throws Exception
	{
		String tagName;
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());
		tagName = "publisher-tag-" + network.getName() + "-" + network.getId();
		return tagName + "__" + dateString; // Leave off ".txt" and ".zip" intentionally
	}

	@RequestMapping(value = "/tags/txt/{networkId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public String grabTags(HttpServletResponse response, @PathVariable Integer networkId) throws Exception
	{
		AccountNetwork network = networkService.getNetworkEntityById(networkId, sessionModel.getUser());
		List<Account> accounts = network.getChildAccounts();
		List<Integer> siteIds = new ArrayList<Integer>();
		for (Account account : accounts)
		{
			if (account.getAccountType() == AccountType.PUBLISHER)
			{
				AccountPublisher publisher = (AccountPublisher)account;
				List<Site> sites = publisher.getSites();
				for (Site site: sites)
				{
					siteIds.add(site.getId());
				}
			}
		}

		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(network, userModel);
		return siteService.grabTags(response, siteIds, userModel, tagName);
	}

	@RequestMapping(value = "/tags/zip/{networkId}", method = RequestMethod.GET, produces = "application/zip")
	@ResponseStatus(HttpStatus.OK)
	@HasUserRole(UserRoleType.USER)
	@Transactional
	public String grabTagsZip(HttpServletResponse response, @PathVariable Integer networkId) throws Exception
	{
		AccountNetwork network = networkService.getNetworkEntityById(networkId, sessionModel.getUser());
		List<Account> accounts = network.getChildAccounts();
		List<Integer> siteIds = new ArrayList<Integer>();
		for (Account account : accounts)
		{
			if (account.getAccountType() == AccountType.PUBLISHER)
			{
				AccountPublisher publisher = (AccountPublisher)account;
				List<Site> sites = publisher.getSites();
				for (Site site: sites)
				{
					siteIds.add(site.getId());
				}
			}
		}

		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(network, userModel);
		return siteService.grabTagsZip(response, siteIds, userModel, tagName);
	}

	// Example http://localhost:8000/wahwahplatform/api/1.0/networks/universaltag/txt/15?xsrf_token=3c2277722bbbad4fafd494ce367ebf646462c8bf
	@RequestMapping(value = "/universaltag/txt/{networkId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public String grabUniversalTag(HttpServletResponse response, @PathVariable Integer networkId) throws Exception
	{
		AccountNetwork network = networkService.getNetworkEntityById(networkId, sessionModel.getUser());
		if (network == null)
		{
			throw new ModelValidationException("Not a valid network id");
		}


		// Temporary set productType and adFormat
		// Todo
		ProductType productType = ProductType.STANDALONE_AD;
		StandaloneAdFormat standaloneAdFormat = StandaloneAdFormat.floater;
		Integer bannerWidth = null;
		Integer bannerHeight = null;

		String tag = "";
		NetworkWebModel networkWebModel = new NetworkWebModel(network);
		try
		{
			tag = networkService.grabUniversalNetworkTag(networkWebModel, sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());

		String tagName = "universal-network-tag-"+ networkWebModel.getName()+"-"+networkWebModel.getAccountId()+"_"+dateString+".txt";


		response.setContentType("text/plain");
		response.addHeader("Content-Disposition", "attachment; filename=\""+tagName+"\"");

		return tag;
	}


}
