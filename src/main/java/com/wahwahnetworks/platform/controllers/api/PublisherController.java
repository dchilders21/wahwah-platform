package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.data.repos.AccountPublisherRepository;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.PublisherWebListModel;
import com.wahwahnetworks.platform.models.web.PublisherWebModel;
import com.wahwahnetworks.platform.models.web.create.PublisherCreateModel;
import com.wahwahnetworks.platform.services.AccountService;
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
@RequestMapping("/api/1.0/publishers")
public class PublisherController extends BaseAPIController
{

	private static final Logger log = Logger.getLogger(SiteController.class);

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private AccountPublisherRepository publisherRepository;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@HasUserRole(UserRoleType.NETWORK_ADMIN)
	public PublisherWebModel createPublisher(@Valid @RequestBody PublisherCreateModel publisherCreateModel, BindingResult result) throws Exception
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}


		if (publisherCreateModel.getName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Publisher name over 255 characters");
		}

		return publisherService.createPublisher(publisherCreateModel, sessionModel.getUser(), false);
	}


	@RequestMapping(value = "/query/{query}", method = RequestMethod.GET, produces = "application/json")
	public PublisherWebListModel search(@PathVariable String query)
	{
		List<PublisherWebModel> publisherWebModelList = publisherService.query(query, sessionModel.getUser());
		return new PublisherWebListModel(publisherWebModelList);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public PublisherWebListModel listPublishers(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "seeAll", defaultValue = "false") Boolean seeAll, @RequestParam(value = "size", defaultValue = "25") Integer pageSize)
	{
		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		Account account = sessionModel.getAccount();
		PublisherWebListModel publisherWebModelList = publisherService.getForParentAccountWithoutDefaults(account,pageable,seeAll);

		// Now have to set red panda creator
		// Todo: Need to map this up in a way that it's automatic like some db mapping. This is slow
		for (PublisherWebModel publisher : publisherWebModelList.getPublishers())
		{
			AccountPublisher accountPublisher = publisherRepository.findOne(publisher.getAccountId());

			if (publisher.getParentAccountId() == null)
			{
				AccountPublisher rpPublisher = publisherRepository.findByMarketplacePublisher(accountPublisher);
				if (rpPublisher != null)
				{
					publisher.setRedPandaPublisherCreatorId(rpPublisher.getId());
				}
			}
			publisher.setCurrentSiteCount(siteService.getSiteCountForPublisherId(publisher.getAccountId()));
		}
		return publisherWebModelList;
	}


	@RequestMapping(value = "/network-{networkId}/", method = RequestMethod.GET, produces = "application/json")
	public PublisherWebListModel listPublishersForNetwork(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize, @PathVariable("networkId") int networkId)
	{
		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		Account account = accountService.findById(networkId);
		if (account.getAccountType() != AccountType.NETWORK)
			return null;
		// Todo: Ensure access to account for sessionModel.getAccount()
		PublisherWebListModel publisherWebModelList = publisherService.getForParentAccountWithoutDefaults(account,pageable, false);
		// Note: network publishers will never have redpanda_publisher_creator_id so don't need setRedPandaPublisherCreatorId like in listPublishers
		return publisherWebModelList;
	}

	@RequestMapping(value = "/{publisherId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public PublisherWebModel updatePublisher(@Valid @RequestBody PublisherWebModel publisherModel, BindingResult result, @PathVariable Integer publisherId) throws Exception
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		if (publisherModel.getName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Publisher name over 255 characters");
		}

		publisherModel.setAccountId(publisherId); /* set the id of the site to be updated */

		log.info(publisherModel.toString());

		return publisherService.updatePublisher(publisherModel, sessionModel.getUser());
	}

	@RequestMapping(value = "/{publisherId}", method = RequestMethod.GET, produces = "application/json")
	public PublisherWebModel getPublisherById(@PathVariable Integer publisherId) throws Exception
	{
		return publisherService.getPublisherById(publisherId, sessionModel.getUser());
	}

	@RequestMapping(value = "/{publisherId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.SUPER_USER)
	// Regular user should not delete his own company's account. Also, require elevated WW user
	public void deletePublisher(@PathVariable("publisherId") int publisherId) throws Exception
	{
		PublisherWebModel publisherModel = publisherService.getPublisherById(publisherId, sessionModel.getUser());

		if (publisherModel == null)
		{
			throw new EntityNotFoundException("Publisher with ID " + publisherId + " does not exist");
		}

		publisherService.deletePublisher(publisherModel, sessionModel.getUser(), true);
	}


	protected String getTagFileName( AccountPublisher publisher, UserModel userModel) throws Exception
	{
		String tagName;
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());
		tagName = "publisher-tag-" + publisher.getName() + "-" + publisher.getId();
		return tagName + "__" + dateString; // Leave off ".txt" and ".zip" intentionally
	}

	@RequestMapping(value = "/tags/txt/{publisherId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public String grabTags(HttpServletResponse response, @PathVariable Integer publisherId) throws Exception
	{
		AccountPublisher publisher = publisherService.getPublisherEntityById(publisherId, sessionModel.getUser());
		List<Site> sites = publisher.getSites();
		List<Integer> siteIds = new ArrayList<Integer>();
		for (Site site: sites)
		{
			siteIds.add(site.getId());
		}

		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(publisher, userModel);
		return siteService.grabTags(response, siteIds, userModel, tagName);
	}

	@RequestMapping(value = "/tags/zip/{publisherId}", method = RequestMethod.GET, produces = "application/zip")
	@ResponseStatus(HttpStatus.OK)
	@HasUserRole(UserRoleType.USER)
	@Transactional
	public String grabTagsZip(HttpServletResponse response, @PathVariable Integer publisherId) throws Exception
	{
		AccountPublisher publisher = publisherService.getPublisherEntityById(publisherId, sessionModel.getUser());
		List<Site> sites = publisher.getSites();
		List<Integer> siteIds = new ArrayList<Integer>();
		for (Site site: sites)
		{
			siteIds.add(site.getId());
		}

		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(publisher, userModel);
		return siteService.grabTagsZip(response, siteIds, userModel, tagName);
	}


	// Example http://localhost:8000/wahwahplatform/api/1.0/publishers/universaltag/txt/15?xsrf_token=3c2277722bbbad4fafd494ce367ebf646462c8bf
	@RequestMapping(value = "/universaltag/txt/{publisherId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String grabUniversalTag(HttpServletResponse response, @PathVariable Integer publisherId) throws Exception
	{
		PublisherWebModel publisherWebModel = publisherService.getPublisherById(publisherId, sessionModel.getUser());
		if (publisherWebModel == null)
		{
			throw new ModelValidationException("Not a valid publisher id.");
		}

		String tag = "";
		try
		{
			tag = publisherService.grabUniversalPublisherTag(publisherWebModel, sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());

		String tagName = "universal-publisher-tag-"+ publisherWebModel.getName()+"-"+publisherWebModel.getAccountId()+"_"+dateString+".txt";


		response.setContentType("text/plain");
		response.addHeader("Content-Disposition", "attachment; filename=\""+tagName+"\"");

		return tag;
	}

}
