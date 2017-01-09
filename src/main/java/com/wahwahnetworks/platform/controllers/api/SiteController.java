package com.wahwahnetworks.platform.controllers.api;

import com.newrelic.api.agent.NewRelic;
import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.enums.ProductType;
import com.wahwahnetworks.platform.data.entities.enums.StandaloneAdFormat;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.ExceptionInfoModel;
import com.wahwahnetworks.platform.models.web.ProductTagListEntry;
import com.wahwahnetworks.platform.models.web.SiteListModel;
import com.wahwahnetworks.platform.models.web.SiteModel;
import com.wahwahnetworks.platform.models.web.create.SiteCreateModel;
import com.wahwahnetworks.platform.services.ProductService;
import com.wahwahnetworks.platform.services.SiteService;
import com.wahwahnetworks.platform.services.SiteTagMigrationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@Scope("request")
@RequestMapping("/api/1.0/sites")
public class SiteController extends BaseAPIController
{
	private static final Logger log = Logger.getLogger(SiteController.class);

	@Autowired
	private SiteService siteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private SiteTagMigrationService siteTagMigrationService;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public SiteModel createSite(@Valid @RequestBody SiteCreateModel siteCreateModel, BindingResult result) throws Exception
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		if (siteCreateModel.getSiteName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Site name is over 255 characters.");
		}


		log.info(siteCreateModel.toString());

		SiteModel newModel = siteService.createSiteWithDefaults(siteCreateModel, sessionModel.getUser(), false);

		try
		{
			siteService.getSiteEntityById(newModel.getId(), sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Error mapping site: " + e.getMessage());
		}

		return newModel;
	}

	@RequestMapping(value = "/{siteId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public SiteModel updateSite(@Valid @RequestBody SiteModel siteModel, BindingResult result, @PathVariable Integer siteId) throws Exception
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		if (siteModel.getSiteName().length() > 255)
		{
			throw new ModelValidationException("Name length error: Site name is over 255 characters.");
		}

		siteModel.setId(siteId); /* set the id of the site to be updated */

		log.info(siteModel.toString());
		return siteService.updateSite(siteModel, sessionModel.getUser(), null /* Doesn't apply here b/c we're only changing country and stuff like that */); // Don't need to use site entity here because not updating externalIds, etc
	}

	@RequestMapping(value = "/{siteId}", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public SiteModel getSiteById(@PathVariable Integer siteId) throws Exception
	{
		return siteService.getSiteById(siteId, sessionModel.getUser());
	}



	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.USER)
	public SiteListModel getSiteList(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize, @RequestParam(value = "highlightSiteId", defaultValue = "-1") Integer highlightSiteId, @RequestParam(value = "seeAll", defaultValue = "false") Boolean seeAll) throws Exception
	{
		// Quick-and-dirty (but inefficient) way to get the page number for current highlighted item. Modifying Pageable class and improving SQL query would probably be more scalable
		// Must be done using sorted list, using same sort that final SiteListModel will contain.
		Pageable pageable;
		if (pageSize == 0)
			pageSize = 25;
		SiteModel highlightSiteModel = null;

		if (highlightSiteId != -1)
		{

			highlightSiteModel = siteService.getSiteById(highlightSiteId, sessionModel.getUser());
			pageable = new PageRequest(0, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "siteName")));
			while (pageable != null)
			{
				SiteListModel slm = siteService.getSiteList(sessionModel.getUser(), pageable, highlightSiteModel, seeAll);
				if (slm.getHighlightOffset() != -1)
				{
					pageNumber = pageable.getPageNumber();
					break;
				}
				pageable = pageable.next();
			}
		}
		else
		{

			pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "siteName")));
		}
		return siteService.getSiteList(sessionModel.getUser(), pageable, highlightSiteModel, seeAll);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "publisher-{publisherId}")
	@HasUserRole(UserRoleType.USER) // SiteService does a separate permissions check
	public SiteListModel getSiteListForPublisher(@PathVariable int publisherId, @RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize, @RequestParam(value = "highlightSiteId", defaultValue = "-1") Integer highlightSiteId)
	{
		SiteModel highlightSiteModel = null;

		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "siteName")));

		return siteService.getSiteListForPublisher(sessionModel.getUser(), publisherId, pageable, highlightSiteModel);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionInfoModel> handleExceptions(HttpServletRequest request, Exception exception)
	{
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		if (exception instanceof ModelValidationException)
		{
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		else if (exception instanceof EntityNotFoundException)
		{
			httpStatus = HttpStatus.NOT_FOUND;
		}
		else if (exception instanceof ServiceException)
		{
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		ExceptionInfoModel exceptionInfo = new ExceptionInfoModel();
		exceptionInfo.setUrl(request.getRequestURL().toString());
		exceptionInfo.setStatus(httpStatus.value());
		exceptionInfo.setMessage(exception.getMessage());

		NewRelic.noticeError(exception);
		log.error(exceptionInfo,exception);

		return new ResponseEntity<>(exceptionInfo, httpStatus);
	}


	@RequestMapping(value = "/{siteId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public void deleteSite(@PathVariable("siteId") int siteId) throws Exception
	{
		SiteModel siteModel = siteService.getSiteById(siteId, sessionModel.getUser());

		if (siteModel == null)
		{
			throw new EntityNotFoundException("Site with ID " + siteId + " does not exist");
		}

		siteService.deleteSite(sessionModel.getUser(), siteModel, true);
	}

	/**
	 * Grab tags
	 */

	@RequestMapping(value = "/tags/txt/product-{productId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String grabTagsForProduct(HttpServletResponse response, @PathVariable Integer productId) throws Exception {

		try {
			ProductTagListEntry tag = productService.grabTag(productId, sessionModel.getUser());
			if (tag != null)
			{
				String tagName = tag.getFileName();

				response.setContentType("text/plain");
				response.addHeader("Content-Disposition", "attachment; filename=\"" + tagName + "\"");

				return tag.getTag();
			}
			else
				return "";

		} catch (Exception e){
			throw new ModelValidationException("Failed Generation Of Tag!" + e.getMessage());
		}

	}


	protected String getTagFileName( List<Integer> siteIds, UserModel userModel) throws Exception
	{
		String tagName;
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());
		if (siteIds.size() == 1)
		{
			Site site = siteService.getSiteEntityById(siteIds.get(0), userModel);

			tagName = "site-tag-" + site.getPublisher().getName() + "__" + site.getSiteName() + "-" + site.getId();
		}
		else
		{
			tagName = "site-tags";
			for (Integer id: siteIds)
			{
				tagName+="_"+id;
			}
		}
		return tagName + "__" + dateString; // Leave off ".txt" and ".zip" intentionally
	}

	@RequestMapping(value = "/tags/txt/{siteIds}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String grabTags(HttpServletResponse response, @PathVariable List<Integer> siteIds) throws Exception
	{
		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(siteIds, userModel);
		return siteService.grabTags(response, siteIds, userModel, tagName);
	}

	@RequestMapping(value = "/tags/zip/{siteIds}", method = RequestMethod.GET, produces = "application/zip")
	@ResponseStatus(HttpStatus.OK)
	@HasUserRole(UserRoleType.USER)
	public String grabTagsZip(HttpServletResponse response, @PathVariable List<Integer> siteIds) throws Exception
	{
		UserModel userModel = sessionModel.getUser();
		String tagName = getTagFileName(siteIds, userModel);
		return siteService.grabTagsZip(response, siteIds, userModel, tagName);
	}


	// Example http://localhost:8000/wahwahplatform/api/1.0/sites/universaltag/txt/15?xsrf_token=3c2277722bbbad4fafd494ce367ebf646462c8bf
	@RequestMapping(value = "/universaltag/txt/{siteId}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String grabUniversalTag(HttpServletResponse response, @PathVariable Integer siteId) throws Exception
	{
		SiteModel siteModel = siteService.getSiteById(siteId, sessionModel.getUser());


		// Temporary set productType and adFormat
		// Todo
		ProductType productType = ProductType.STANDALONE_AD;
		StandaloneAdFormat standaloneAdFormat = StandaloneAdFormat.floater;
		Integer bannerWidth = null;
		Integer bannerHeight = null;

		String tag = "";
		try
		{
			tag = siteService.grabUniversalSiteTag(siteModel, sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date());

		String tagName = "universal-site-tag-"+ siteModel.getSiteName()+"_"+dateString+".txt";


		response.setContentType("text/plain");
		response.addHeader("Content-Disposition", "attachment; filename=\""+tagName+"\"");

		return tag;
	}


}