package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.AdUnit;
import com.wahwahnetworks.platform.data.entities.ProductVersion;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.models.BulkPublishModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.AdUnitListModel;
import com.wahwahnetworks.platform.models.web.ProductListModel;
import com.wahwahnetworks.platform.models.web.ProductModel;
import com.wahwahnetworks.platform.models.web.ProductTagListModel;
import com.wahwahnetworks.platform.services.AuditService;
import com.wahwahnetworks.platform.services.BulkPublishService;
import com.wahwahnetworks.platform.services.ProductService;
import com.wahwahnetworks.platform.services.ProductVersionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@Scope("request")
@RequestMapping("/api/1.0/products")


/**
 * Created by Brian.Bober on 12/22/2014.
 */

public class ProductController extends BaseAPIController
{
	private static final Logger log = Logger.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductVersionService productVersionService;

	@Autowired
	private BulkPublishService bulkPublishService;

	@Autowired
	private SessionModel sessionModel;

	@Autowired
	private AuditService auditService;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public ProductListModel getProductList(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize)
	{
		Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		return productService.getProductListForSite(sessionModel.getUser(), pageable);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public ProductModel createProduct(@Valid @RequestBody ProductModel productModel, BindingResult result)
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		log.info(productModel.toString());

		// Note: We DON'T call openxService.createAdUnits here because this can also be called using the "Create Tag" form with set AUIDs
		// We may need to rethink this if we bring back "Create Tag" since right now we always createWithDefaults

		ProductModel product = productService.createProduct(productModel, sessionModel.getUser(), null);


		return product;
	}

	@RequestMapping(value = "/{productId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public ProductModel updateProduct(@Valid @RequestBody ProductModel productModel, BindingResult result, @PathVariable Integer productId)
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}
		productModel.setId(productId); /* set the id of the product to be updated */

		if (productModel.getIsLocked()) {
			productModel.setLockedUserId(sessionModel.getUser().getUserId()); /* sets the ID of the User if they lock the settings */
		}

		log.info(productModel.toString());
		return productService.updateProduct(productModel, sessionModel.getUser());
	}

	@RequestMapping(value = "/{productId}/publish", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public void publishProduct(@PathVariable Integer productId) throws Exception
	{
		ProductModel productModel = productService.getProductById(productId, sessionModel.getUser());
		productService.requestPublishProduct(productModel.getId());
	}

	@RequestMapping(value = "/bulkpublish", method = RequestMethod.POST)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public BulkPublishModel bulkPublish(@RequestBody BulkPublishModel bulkPublishModel) throws Exception
	{
		bulkPublishService.updateProduct(sessionModel.getUser(), bulkPublishModel); // This method will be invoked async
		for (int i = 0; i < bulkPublishModel.getProductIds().size(); i++)
		{
			ProductModel productModel = productService.getProductById(bulkPublishModel.getProductIds().get(i),sessionModel.getUser());
			auditService.addAuditEntry(AuditActionTypeEnum.PRODUCT_PUBLISH, "Bulk Published  " + productModel.getName(), "");
		}
		return new BulkPublishModel();
	}

	@RequestMapping(value = "/{productId}", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public ProductModel getProductById(@PathVariable Integer productId)
	{
		return productService.getProductById(productId, sessionModel.getUser());
	}

	@RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public void deleteProductById(@PathVariable Integer productId)
	{
		productService.deleteProductById(productId, sessionModel.getUser(), true);
	}

	@RequestMapping(value = "site-{siteId}", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public ProductListModel getProductList(@PathVariable Integer siteId)
	{
		return productService.getProductListForSite(siteId, sessionModel.getUser());
	}

	@RequestMapping(value = "version-{gitBranchName}", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.TOOLBAR_PUBLISHER)
	public ProductListModel getProductList(@PathVariable String gitBranchName)
	{
		ProductVersion productVersion = productVersionService.findByGitBranchName(gitBranchName);
		return productService.getProductListForSite(productVersion, sessionModel.getUser());
	}

	/**
	 * Grab tags
	 */

	@RequestMapping(value = "/tags/txt/{productTagIds}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String grabTagsTxt(@PathVariable("productTagIds") List<Integer> productTagIds, BindingResult result)
	{
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		ProductTagListModel tags = null;
		try
		{
			tags = productService.grabTags(productTagIds, sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		return productService.createTagTextResponse(tags);
	}

	@RequestMapping(value = "/tags/zip/{productTagIds}", method = RequestMethod.GET, produces = "application/zip")
	@ResponseStatus(HttpStatus.OK)
	public String grabTagsZip(@PathVariable("productTagIds") List<Integer> productTagIds, HttpServletResponse response) throws Exception
	{

		ProductTagListModel tags = null;
		try
		{
			tags = productService.grabTags(productTagIds, sessionModel.getUser());
		}
		catch (Exception e)
		{
			throw new ModelValidationException("Failed generation of tag! " + e.getMessage());
		}

		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment; filename=\"tags.zip\"");
		response.addHeader("Content-Transfer-Encoding", "binary");

		productService.createTagZipResponse(tags, response);

		return "";
	}


	/**
	 * The following four methods are for passbacks. These are ad units set up in OpenX, etc that are not
	 * created through platform and not required for a particular tag (product). They are also not managed
	 * by the platform other than allowing user to manually add/remove them for use by analytics. All logic
	 * to call these passbacks are set up in the ad server directly. Analytics just needs to know about them.
	 */


	@RequestMapping(value = "{productId}/ad-units", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public AdUnitListModel getAdUnits(@PathVariable Integer productId)
	{
		ProductModel product = productService.getProductById(productId, sessionModel.getUser());
		return productService.getAdUnitsListModelByProduct(product, sessionModel.getUser());
	}


	@RequestMapping(value = "/adunits/{productId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	@ResponseStatus(value = HttpStatus.OK)
	public AdUnitListModel updateAdUnits(@PathVariable Integer productId, @Valid @RequestBody AdUnitListModel adUnitListModel, BindingResult result)
	{
		// For passbacks only
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		ProductModel product = productService.getProductById(productId, sessionModel.getUser());
		List<AdUnit> adUnitList = productService.convertAdUnitListModelToAdUnitList(adUnitListModel.getAdUnits());
		Iterable<AdUnit> saved = productService.updatePassbackAdUnits(adUnitList, product, sessionModel.getUser());
		return productService.convertAdUnitListToAdUnitListModel(product,saved);
	}

	@RequestMapping(value = "/adunits/{productId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public AdUnitListModel createAdUnits(@Valid @RequestBody AdUnitListModel adUnitListModel, @PathVariable Integer productId, BindingResult result)
	{
		// For passbacks only
		if (result.hasErrors())
		{
			throw new ModelValidationException(result.toString());
		}

		ProductModel product = productService.getProductById(productId, sessionModel.getUser());
		List<AdUnit> adUnitList = productService.convertAdUnitListModelToAdUnitList(adUnitListModel.getAdUnits());
		Iterable<AdUnit> saved = productService.createPassbackAdUnits(adUnitList, product, sessionModel.getUser());
		return productService.convertAdUnitListToAdUnitListModel(product,saved);
	}


	@RequestMapping(value = "/adunits/product-{productId}/adunit-{adUnitId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.PUBLISHER_ADMIN)
	public void deleteAdUnits(@PathVariable Integer productId, @PathVariable Integer adUnitId)
	{
		ProductModel product = productService.getProductById(productId, sessionModel.getUser());
		AdUnit adUnit = productService.getAdUnitById(adUnitId);
		if (adUnit.getProduct().getId() != productId)
		{
			throw new ModelValidationException("Ad unit id doesn't exist for product id");
		}
		productService.deletePassbackAdUnits(adUnit, product, sessionModel.getUser());
		return;
	}


}