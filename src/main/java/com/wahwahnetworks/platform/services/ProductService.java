package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.*;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.lib.NameUtils;
import com.wahwahnetworks.platform.models.TagSettingsCreateModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.*;
import com.wahwahnetworks.platform.services.rabbit.DelayedExchangeService;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by Brian.Bober on 12/22/2014.
 */

@Service
public class ProductService
{
	private static final Logger log = Logger.getLogger(ProductService.class);

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private AdUnitRepository adUnitRepository;

	@Autowired
	private ClientFeatureRepository clientFeatureRepository;

	@Autowired
	private SiteService siteService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private GenerationService generationService;

	@Autowired
	private ProductVersionService productVersionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RevenueModelService revenueModelService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private DelayedExchangeService delayedExchangeService;

    @Autowired
    private Environment environment;

	@Transactional
	public ProductModel createProductWithDefaults(Site site, String productName, UserModel userModel, Boolean isRPNetwork, Boolean isMarketplace, ProductFormat format)
	{
		if (StringUtils.isEmpty(productName))
		{
			throw new ServiceException("Product name cannot be empty string!");
		}

		if (format == null)
		{
			if (isRPNetwork && isMarketplace)
			{
				format = ProductFormat.BANNER;
			}
			else
			{
				format = ProductFormat.FLOATER;
			}
		}

		ProductModel productModel = new ProductModel();

		productModel.setSite(site.getId());
		productModel.setIsDebug(false);

        AdConfigInPageModel adConfigInPageModel = new AdConfigInPageModel();
        adConfigInPageModel.setPrimaryDisplayAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setLeaveBehindAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setVideoAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setBackupDisplayAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setTabletAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setMobileAdServerType(AdServerType.OPEN_X.toString());
        adConfigInPageModel.setBannerAdsPerVideoAd(4);
        adConfigInPageModel.setEnabled(true);
        adConfigInPageModel.setDisplayWidth(300);
        adConfigInPageModel.setDisplayHeight(250);
        adConfigInPageModel.setVideoWidth(558);
        adConfigInPageModel.setVideoHeight(314);
        adConfigInPageModel.setSecondsBetweenBanners(615);
        adConfigInPageModel.setSecondsToExpand(15);
        adConfigInPageModel.setPerSessionCap(15);
        adConfigInPageModel.setVidCountdownNumFormat("${SEC}");
        adConfigInPageModel.setFreqCapEnabled(false);
        adConfigInPageModel.setAudioVolume(0);

        productModel.setAdConfigInPage(adConfigInPageModel);

        boolean isCustomProduct = false;
        boolean isStandAloneAdProduct = false;
        boolean isAdServerNativeProduct = false;

        switch(format){
            case CUSTOM:
                isCustomProduct = true;
                break;
            case BANNER:
            case FLOATER:
            case OUTSTREAM:
                isStandAloneAdProduct = true;
                break;
            case ADSERVERNATIVE:
                isAdServerNativeProduct = true;
                break;
        }

		if (isStandAloneAdProduct)
		{
            ProductStandaloneAdModel standalone = new ProductStandaloneAdModel();

			productModel.setProductType(ProductType.STANDALONE_AD);
            standalone.setAlignHorizontal(AlignHorizontal.right);
			standalone.setAlignVertical(AlignVertical.bottom);
			standalone.setExpansionAlignHorizontal(AlignHorizontal.right);
			standalone.setExpansionAlignVertical(AlignVertical.bottom);
			standalone.setBreakoutAd(Boolean.FALSE);
			standalone.setOutstreamAutoload(Boolean.FALSE);
			standalone.setOutstreamFloat(Boolean.FALSE);
			standalone.setOutstreamTriggerId("");

			if (format == ProductFormat.BANNER)
			{
				standalone.setAdFormat(StandaloneAdFormat.banner);
			}
			else if (format == ProductFormat.FLOATER)
			{
				standalone.setAdFormat(StandaloneAdFormat.floater);
			}
			else if (format == ProductFormat.OUTSTREAM)
			{
				standalone.setAdFormat(StandaloneAdFormat.ostream);
			}

            productModel.setStandaloneAd(standalone);
		}

        if (isRPNetwork && isMarketplace)
        {
            productName += NameUtils.mpSuffix;
        }

        if(isCustomProduct)
		{
			productModel.setProductType(ProductType.CUSTOM);
			productModel.setIsDebug(true);
		}

        if(isAdServerNativeProduct)
        {
            productModel.setProductType(ProductType.AD_SERVER_NATIVE);
            productModel.setIsDebug(false);
        }

		productModel.setIsLocked(Boolean.FALSE);
		productModel.setLogLevel(WWLogLevel.info);
		ProductVersion productVersion = productVersionService.getDefaultVersion();
		productModel.setProductVersion(productVersion);
		productModel.setName(productName + " Ad");

		// Generate ox products
		// TODO - make asynchronous

		Map<String, String> externalId2s = null; // We don't need these external ids going to UI

		if (site.getExternalId() != null && isStandAloneAdProduct)
		{

		}

		log.info(productModel.toString());
		return createProduct(productModel, userModel, externalId2s);
	}

	@Transactional
	public ProductModel createDefaultTagsProduct(Site site, UserModel userModel, Boolean isRPNetwork, Boolean isMarketplace)
	{
		// Banner
		String productName = site.getSiteName();
		ProductModel productModelBanner = createProductWithDefaults(site, NameUtils.modifyNameWithPrependAndSuffix(productName,255,"Default Banner Tag - ",""), userModel, isRPNetwork, isMarketplace, ProductFormat.FLOATER);
		Product bannerProduct = productRepository.getOne(productModelBanner.getId());
		bannerProduct.setDefault(true);
		productRepository.save(bannerProduct);
        setWidgetId(bannerProduct);

        // Floater
		ProductModel productModelFloater = createProductWithDefaults(site, NameUtils.modifyNameWithPrependAndSuffix(productName,255,"Default Floater Tag - ",""), userModel, isRPNetwork, isMarketplace, ProductFormat.BANNER);
		Product floaterProduct = productRepository.getOne(productModelFloater.getId());
		floaterProduct.setDefault(true);
		productRepository.save(floaterProduct);
        setWidgetId(floaterProduct);

        // Outstream
		ProductModel productModelOutstream = createProductWithDefaults(site, NameUtils.modifyNameWithPrependAndSuffix(productName,255,"Default Outstream Tag - ",""), userModel, isRPNetwork, isMarketplace, ProductFormat.OUTSTREAM);
		Product outstreamProduct = productRepository.getOne(productModelOutstream.getId());
		outstreamProduct.setDefault(true);
		productRepository.save(outstreamProduct);
        setWidgetId(outstreamProduct);

		Product defaultProduct = outstreamProduct; // Outstream
		if (isRPNetwork && isMarketplace)
		{
			defaultProduct = bannerProduct;
		}
		site.setDefaultProduct(defaultProduct);
		siteRepository.save(site);

		// We can't grab tags later, etc, so need to be published now
		requestPublishProduct(productModelBanner.getId());
		requestPublishProduct(productModelFloater.getId());


		requestPublishProduct(productModelOutstream.getId());

		return getProductById(defaultProduct.getId(),userModel);
	}


	@Transactional
	public void updateDefaultTagsProduct(Site site, TagSettingsProductModel firstNonDefaultTag /* Can be null for marketplace products part of a network*/)
	{
		// Banner
		String baseName = site.getSiteName();
		final List<Product> products = site.getProducts();
		for (Product product: products)
		{
			if (product.isDefault())
			{
					ProductFormat format = ProductFormat.CUSTOM;

					if (firstNonDefaultTag != null)
					{
						// This only happens for default tags here. See TagSettingsController.updateTagSettingsForSite for the rest
						// Apply version # to standard tags. Todo - replace some of this w/ PLATFORM-957 and PLATFORM-926
						ProductModel firstNonDefaultTagProductModel = firstNonDefaultTag.getProductModel();
						product.setProductVersion(firstNonDefaultTagProductModel.getProductVersion());
						product.setLogLevel(firstNonDefaultTagProductModel.getLogLevel());
						product.setDebug(firstNonDefaultTagProductModel.getDebug());
					}

                    if (product.getProductStandaloneAd() != null)
					{
						final StandaloneAdFormat adFormat = product.getProductStandaloneAd().getAdFormat();
						if (adFormat.equals(StandaloneAdFormat.ostream))
						{
							format = ProductFormat.OUTSTREAM;
							String productName = NameUtils.modifyNameWithPrependAndSuffix(baseName,255,"Default Outstream Tag - ","");
							product.setName(productName);
							productRepository.save(product);

                            setWidgetId(product);
							requestPublishProduct(product.getId());
						}
						else if (adFormat.equals(StandaloneAdFormat.banner))
						{
							format = ProductFormat.BANNER;
							String productName = NameUtils.modifyNameWithPrependAndSuffix(baseName,255,"Default Banner Tag - ","");
							product.setName(productName);
							productRepository.save(product);

                            setWidgetId(product);
							requestPublishProduct(product.getId());
						}
						else if (adFormat.equals(StandaloneAdFormat.floater))
						{
							format = ProductFormat.FLOATER;
							String productName = NameUtils.modifyNameWithPrependAndSuffix(baseName,255,"Default Floater Tag - ","");
							product.setName(productName);
							productRepository.save(product);

                            setWidgetId(product);
							requestPublishProduct(product.getId());
						}
					}
			}
		}
	}

	@Transactional
	public ProductModel createAdditionalProduct(UserModel userModel, Site site, TagSettingsCreateModel createModel){

        // Check Marketplace Data
        boolean isRedPandaNetworkSite = false;
        boolean isMarketplaceSiteForRedPandaNetworkSite = false;

        if(site.getPublisher().getParentAccount() != null){
            isRedPandaNetworkSite = true;
        }

        if(siteService.getRedPandaSiteCreator(site) != null){
            isMarketplaceSiteForRedPandaNetworkSite = true;
        }

        boolean isRedPandaNetwork = isRedPandaNetworkSite || isMarketplaceSiteForRedPandaNetworkSite;

		return createProductWithDefaults(site,createModel.getName(),userModel,isRedPandaNetwork,isMarketplaceSiteForRedPandaNetworkSite, createModel.getProductFormat());
	}

	@Transactional
	public ProductModel createProduct(ProductModel productModel, UserModel userModel, Map<String, String> externalId2s)
	{
		if (productModel.getName() == "")
		{
			throw new ServiceException("Product name cannot be empty string!");
		}
		Site site;
		try
		{
			site = siteService.getSiteEntityById(productModel.getSite(), userModel);
		}
		catch (Exception exc)
		{
			log.error("Error updating product", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (site == null)
		{
			throw new EntityNotFoundException(String.format(
					"Site (id %d) not found, cannot update product", productModel.getId(), productModel.getSite()));
		}


		try
		{

			Product product = productFromModel(null, productModel, site);

			if (externalId2s != null)
			{
				Iterator it = externalId2s.entrySet().iterator();
				while (it.hasNext())
				{
					Map.Entry<String, String> pair = (Map.Entry) it.next();
					if (pair.getKey().equals("PrimaryDisplay") && product.getAdConfigInPage().getPrimaryDisplayAdUnit() != null)
						product.getAdConfigInPage().getPrimaryDisplayAdUnit().setAdServerUnitId2(pair.getValue());
					else if (pair.getKey().equals("LeaveBehind") && product.getAdConfigInPage().getLeaveBehindAdUnit() != null)
						product.getAdConfigInPage().getLeaveBehindAdUnit().setAdServerUnitId2(pair.getValue());
					else if (pair.getKey().equals("Video") && product.getAdConfigInPage().getVideoAdUnit() != null)
						product.getAdConfigInPage().getVideoAdUnit().setAdServerUnitId2(pair.getValue());
					else if (pair.getKey().equals("BackupDisplay") && product.getAdConfigInPage().getBackupDisplayAdUnit() != null)
						product.getAdConfigInPage().getBackupDisplayAdUnit().setAdServerUnitId2(pair.getValue());
					else if (pair.getKey().equals("Tablet") && product.getAdConfigInPage().getTabletAdUnit() != null)
						product.getAdConfigInPage().getTabletAdUnit().setAdServerUnitId2(pair.getValue());
					else if (pair.getKey().equals("Mobile") && product.getAdConfigInPage().getMobileAdUnit() != null)
						product.getAdConfigInPage().getMobileAdUnit().setAdServerUnitId2(pair.getValue());
				}
			}


			productRepository.save(product);
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getPrimaryDisplayAdUnit());
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getBackupDisplayAdUnit());
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getVideoAdUnit());
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getMobileAdUnit());
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getTabletAdUnit());
			adUnitRepositorySaveHelper(adUnitRepository, product.getAdConfigInPage().getLeaveBehindAdUnit());

			/* set generated product id in returned data */
			productModel.setId(product.getId());

			auditService.addAuditEntry(AuditActionTypeEnum.PRODUCT_CREATE, "Created " + product.getName() + " for Site: " + product.getSite().getSiteName(), "");

			return getProductById(product.getId(),userModel);

		}
		catch (Exception exc)
		{
			log.error("Error creating product", exc);
			throw exc;
		}

	}

	@Transactional
	private void adUnitRepositorySaveHelper(AdUnitRepository r, AdUnit a)
	{
		if (a == null)
			return;
		r.save(a);

	}

	@Transactional
	public ProductModel updateProduct(ProductModel productModel, UserModel userModel)
	{
		Product product;
		Site site;
		try
		{
			product = productRepository.findOne(productModel.getId());
			site = siteService.getSiteEntityById(productModel.getSite(), userModel);
		}
		catch (Exception exc)
		{
			log.error("Error updating product", exc);
			throw new ServiceException(exc.getMessage());
		}

		if (product.getLocked() == true)
		{
			log.warn("Not updating product " + product.getName() + " ("+ product.getId() + ") because it is locked.");
			return getProductById(product.getId(), userModel);
		}
		if (product == null || site == null)
		{
			throw new EntityNotFoundException(String.format(
					"Product (id %d) or site (id %d) not found, cannot update product", productModel.getId(), productModel.getSite()));
		}
		if (product.getSite().getPublisher().getId() != site.getPublisher().getId())
		{
			throw new ServiceException(String.format(
					"Product (id %d) does not belong to same account as site (id %d), cannot update product", productModel.getId(),
					productModel.getSite()));
		}
		if (product.getProductType() == ProductType.CUSTOM && productModel.getProductType() != ProductType.CUSTOM)
		{
			throw new ServiceException(String.format(
					"Cannot change product type from CUSTOM to anything else!"));
		}

		try
		{
			// It's IMPORTANT that we pass in the SAME product as pulled from db because Hibernate internals require
			// it or it fails thinking you are creating new product with same id!!!
			productFromModel(product, productModel, site);

/*
		Do not remove the 1st or 2nd save below or everything breaks.

		So, Justin figured out how to make changing product type work. Basically, hibernate was clearing
		the product property after the save (somehow mucking up the mapping) but then setting it again and saving it again
		fixed it. His explanation of the code below, un-edited in completely raw:

		Me: Set property to bla
		Hibernate: Dunno what we are doing, set property to null
		Me: No really, set the damned property to bla
		Hibernate: ok, you actually want property to be bla.. let's save that

*/

			productRepository.save(product);

			if (product.getAdConfigInPage().getPrimaryDisplayAdUnit() != null)
				adUnitRepository.save(product.getAdConfigInPage().getPrimaryDisplayAdUnit());
			if (product.getAdConfigInPage().getBackupDisplayAdUnit() != null)
				adUnitRepository.save(product.getAdConfigInPage().getBackupDisplayAdUnit());
			if (product.getAdConfigInPage().getVideoAdUnit() != null)
				adUnitRepository.save(product.getAdConfigInPage().getVideoAdUnit());
			if (product.getAdConfigInPage().getMobileAdUnit() != null)
				adUnitRepository.save(product.getAdConfigInPage().getMobileAdUnit());
			if (product.getAdConfigInPage().getTabletAdUnit() != null)
				adUnitRepository.save(product.getAdConfigInPage().getTabletAdUnit());
			if (product.getAdConfigInPage().getLeaveBehindAdUnit() != null) /* Todo: Temporary solution */
				adUnitRepository.save(product.getAdConfigInPage().getLeaveBehindAdUnit());

			// Ensure Entity Relations
			if (product.getProductStandaloneAd() != null)
			{
				product.getProductStandaloneAd().setProduct(product);
			}

			if (product.getProductToolbar() != null)
			{
				product.getProductToolbar().setProduct(product);
			}

			if (product.getAdConfigInPage() != null)
			{
				product.getAdConfigInPage().setProduct(product);
			}

			productRepository.save(product);

			auditService.addAuditEntry(AuditActionTypeEnum.PRODUCT_SAVE, "Saved " + product.getName() + " for Site: " + product.getSite().getSiteName(), "");

			if (product.getSite().getMarketplaceSite() != null)
			{
				// This has a marketplace site, so we need to propagate some things from product to marketplace tag
				// Todo: Need to do more here for support multiple tags per site
				Iterable<Product> bySiteId = productRepository.findBySiteId(product.getSite().getMarketplaceSite().getId());
				Product linkedMarketplaceProduct = bySiteId.iterator().next();

				// Change certain values
				linkedMarketplaceProduct.setProductVersion(product.getProductVersion());
				linkedMarketplaceProduct.setName(product.getName() + NameUtils.mpSuffix);


				// This has a marketplace site, so let's publish that too
                RevenueModel revenueModel = revenueModelService.getRevenueModelForProduct(linkedMarketplaceProduct);
				ProductModel linkedMarketplaceModel = new ProductModel(linkedMarketplaceProduct,revenueModel);

				updateProduct(linkedMarketplaceModel, userModel);

				auditService.addAuditEntry(AuditActionTypeEnum.PRODUCT_SAVE, "Updated linked " + linkedMarketplaceProduct.getName() + " for Site: " + linkedMarketplaceProduct.getSite().getSiteName() + " linked to already published " + product.getName(), "");

			}

			if (product.getProductType() != ProductType.CUSTOM && product.getProductType() != ProductType.AD_SERVER_NATIVE)
			{
				productRepository.save(product);
			}

			setWidgetId(product);
			requestPublishProduct(product.getId());

			return getProductById(product.getId(), userModel);

		}
		catch (Exception exc)
		{
			log.error("Error creating product", exc);
			throw new ServiceException(exc.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ProductListModel getProductListForSite(UserModel userModel, Pageable pageable)
	{
		Page<Product> products;

		try
		{
			if (userModel.hasRole(UserRoleType.SUPER_USER))
			{
				products = productRepository.findAll(pageable);
			}
			else
			{
				products = productRepository.findBySitePublisherId(userModel.getAccountId(), pageable);
			}

		}
		catch (Exception exc)
		{
			log.error("Error getting product list", exc);
			throw new ServiceException(exc.getMessage());
		}

		return new ProductListModel(products);
	}

	@Transactional(readOnly = true)
	public ProductListModel getProductListForSite(Integer siteId, UserModel userModel)
	{
		Iterable<Product> products = null;
		try
		{
			/* verify that the logged in user can see this site */
			Site site = siteService.getSiteEntityById(siteId, userModel);
			if (site != null)
			{
				products = productRepository.findBySiteId(siteId);
			}
		}
		catch (Exception exc)
		{
			log.error("Error getting product list", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (products != null)
		{
			return new ProductListModel(products);
		}
		else
		{
			throw new EntityNotFoundException(String.format("Products list for site id %d and user id %d not found",
					siteId, userModel.getUserId()));
		}
	}

	@Transactional(readOnly = true)
	public ProductListModel getProductListForPublisher(Integer publisherId, UserModel userModel) throws Exception
	{
		try
		{
			/* verify that the logged in user can see this site */
			PublisherWebModel publisher = publisherService.getPublisherById(publisherId, userModel);

			ProductListModel list = new ProductListModel(new ArrayList<>());

			Pageable pageable = new PageRequest(0, RedPandaConstants.MaximumPageSize);

			SiteListModel siteListForPublisher = siteService.getSiteListForPublisher(userModel, publisherId, pageable, null);
			List<SiteModel> sites = siteListForPublisher.getSites();
			for (int i = 0; i < sites.size(); i++)
			{
				ProductListModel tmpList = getProductListForSite(sites.get(i).getId(), userModel);
				List<ProductModel> products = tmpList.getProducts();
				for (int j = 0; j < products.size(); j++)
					list.add(products.get(j));
			}
			return list;
		}
		catch (Exception exc)
		{
			log.error("Error getting product list for publisher " + publisherId, exc);
			throw new ServiceException(exc.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ProductListModel getProductListForSite(ProductVersion productVersion, UserModel userModel)
	{
		if (userModel.hasRole(UserRoleType.TOOLBAR_PUBLISHER))
		{
			Iterable<Product> products = productRepository.findByProductVersion(productVersion);
			return new ProductListModel(products);
		}

		throw new EntityNotPermittedException("You do not have permissions to use this method");
	}


	@Transactional(readOnly = true)
	public Product getProductEntityById(int productId, UserModel userModel)
	{
		Product product;
		try
		{
			product = productRepository.findOne(productId);
		}
		catch (Exception exc)
		{
			log.error("Error getting site by id", exc);
			return null;
		}
		return product;
	}

	@Transactional(readOnly = true)
	public ProductModel getProductById(int productId, UserModel userModel)
	{
		Product product = getProductEntityById(productId, userModel);
		if (product != null)
		{
            RevenueModel revenueModel = revenueModelService.getRevenueModelForProduct(product);
			return new ProductModel(product,revenueModel);
		}
		else
		{
			throw new EntityNotFoundException("Product with id " + productId + " not found");
		}
	}

	@Transactional
	public void deleteProductById(int productId, UserModel userModel, Boolean defaultEntryCheck)
	{
		Product product;
		try
		{
			product = productRepository.findOne(productId);
		}
		catch (Exception exc)
		{
			log.error("Error getting site by id", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (product == null)
			return; // shouldn't happen

		if (product.isDefault() && defaultEntryCheck == true)
		{
			log.warn("Skipping delete of "+productId+". Default product detected with defaultEntryCheck enabled. ");
		}
		// Archive openx
		product.setArchived(true);
		productRepository.save(product);
        revenueModelService.deleteBillableEntityForProduct(product);

		productRepository.delete(product);
	}


	@Transactional
	private Product productFromModel(Product product, ProductModel productModel, Site site /* prevent excessive API requests */)
	{
		if (product == null){
            product = new Product();
        }

        product.setWidgetId(productModel.getWidgetId());
		product.setProductType(productModel.getProductType());

        AdConfigInPage adConfigInPage;

        if (product.getAdConfigInPage() != null)
        {
            adConfigInPage = product.getAdConfigInPage();
        }
        else
        {
            adConfigInPage = new AdConfigInPage(product);
        }

        adConfigInPage.updateFromModel(productModel.getAdConfigInPage());

        product.setAdConfigInPage(adConfigInPage);

		if (product.getProductType() == ProductType.TOOLBAR || product.getProductType() == ProductType.MINI_BAR || product.getProductType() == ProductType.CUSTOM || product.getProductType() == ProductType.AD_SERVER_NATIVE)
		{
			product.setProductStandaloneAd(null);

            if(product.getProductType() == ProductType.TOOLBAR || product.getProductType() == ProductType.MINI_BAR || product.getProductType() == ProductType.CUSTOM){
                ProductToolbarModel toolbarModel = productModel.getToolbar();

                ProductToolbar toolbar;

                if (product.getProductToolbar() != null)
                {
                    toolbar = product.getProductToolbar();
                }
                else
                {
                    toolbar = new ProductToolbar(product);
                }

                toolbar.setAlignHorizontal(toolbarModel.getAlignHorizontal());
                toolbar.setAlignVertical(toolbarModel.getAlignVertical());
                toolbar.setRadioAlign(toolbarModel.getRadioAlign());
                toolbar.setRadioType(toolbarModel.getRadioType());
                toolbar.setSkinType(toolbarModel.getSkinType());
                toolbar.setLoadPreference(toolbarModel.getLoadPreference());
                product.setProductToolbar(toolbar);
            }

			if (product.getProductType() == ProductType.CUSTOM || product.getProductType() == ProductType.AD_SERVER_NATIVE)
			{

				adConfigInPage.setPrimaryDisplayAdUnit(null);
				adConfigInPage.setBackupDisplayAdUnit(null);
				adConfigInPage.setTabletAdUnit(null);
				adConfigInPage.setVideoAdUnit(null);
				adConfigInPage.setMobileAdUnit(null);
				adConfigInPage.setLeaveBehindAdUnit(null);
			}
		}
		else if (product.getProductType() == ProductType.STANDALONE_AD)
		{
			product.setProductToolbar(null);
			ProductStandaloneAdModel standaloneAdModel = productModel.getStandaloneAd();

			product.setProductToolbar(null);

			ProductStandaloneAd standaloneAd;

			if (product.getProductStandaloneAd() != null)
			{
				standaloneAd = product.getProductStandaloneAd();
			}
			else
			{
				standaloneAd = new ProductStandaloneAd(product);
			}

			standaloneAd.setAlignVertical(standaloneAdModel.getAlignVertical());
			standaloneAd.setAlignHorizontal(standaloneAdModel.getAlignHorizontal());
			standaloneAd.setExpansionAlignHorizontal(standaloneAdModel.getExpansionAlignHorizontal());
			standaloneAd.setExpansionAlignVertical(standaloneAdModel.getExpansionAlignVertical());
			standaloneAd.setBreakoutAd(standaloneAdModel.getBreakoutAd());
			standaloneAd.setOutstreamAutoload(standaloneAdModel.getOutstreamAutoload());
			standaloneAd.setOutstreamTriggerId(standaloneAdModel.getOutstreamTriggerId());
			standaloneAd.setOutstreamFloat(standaloneAdModel.getOutstreamFloat());
			standaloneAd.setAdFormat(standaloneAdModel.getAdFormat());
			product.setProductStandaloneAd(standaloneAd);

		}
		else
		{
			// shouldn't happen
			throw (new ServiceException("Invalid Product Type"));
		}

		if(productModel.getFeatures() != null) {
			for (ProductClientFeatureModel featureModel : productModel.getFeatures()) {

				Boolean featureAlreadySaved = false;

				ProductClientFeature productClientFeature = null;

				for (ProductClientFeature clientFeature : product.getFeatures()) {
					if (clientFeature.getClientFeature().getId() == featureModel.getFeatureId()) {
						featureAlreadySaved = true;
						productClientFeature = clientFeature;
					}
				}

				if (!featureAlreadySaved) {

					ClientFeature clientFeature = clientFeatureRepository.findOne(featureModel.getFeatureId());

					productClientFeature = new ProductClientFeature();
					productClientFeature.setClientFeature(clientFeature);
					productClientFeature.setProduct(product);
					product.getFeatures().add(productClientFeature);
				}

				productClientFeature.setVariableValueBoolean(featureModel.getValueBoolean());
				productClientFeature.setVariableValueNumber(featureModel.getValueNumber());
				productClientFeature.setVariableValueString(featureModel.getValueString());

			}

			List<ProductClientFeature> featuresToRemove = new ArrayList<>();

			for (ProductClientFeature clientFeature : product.getFeatures()) {
				Boolean featureStillExists = false;

				for (ProductClientFeatureModel featureModel : productModel.getFeatures()) {
					if (featureModel.getFeatureId() == clientFeature.getClientFeature().getId()) {
						featureStillExists = true;
					}
				}

				if (!featureStillExists) {
					featuresToRemove.add(clientFeature);
				}
			}

			for (ProductClientFeature productClientFeature : featuresToRemove) {
				product.getFeatures().remove(productClientFeature);
			}
		}

		product.setLocked(productModel.getIsLocked());

		if(productModel.getLockedUserId() != null){
			User user = userRepository.findOne(productModel.getLockedUserId());
			product.setLockedUser(user);
		} else {
			product.setLockedUser(null);
		}

		product.setLogLevel(productModel.getLogLevel());
		product.setIsDebug(productModel.getIsDebug());
		product.setName(productModel.getName());
		product.setSite(site);
		product.setProductVersion(productModel.getProductVersion());
		product.setId(productModel.getId());

		return product;
	}

    private void setWidgetId(Product product){
		if(product.getWidgetId() == null) {
			productRepository.save(product);

			RestTemplate restTemplate = new RestTemplate();
			ProductWidgetModel widget = restTemplate.getForObject("http://app.redpandaplatform.com/api/1.0/widget/" + environment.name() + "/" + product.getId(), ProductWidgetModel.class);

			product.setWidgetId(widget.getWidgetId());
			productRepository.save(product);
		}
    }

	private Product setProductVersion(Product product)
	{
		if (product.getProductVersion() == null)
		{
			product.setProductVersion(productVersionService.getDefaultVersion());
			if (!product.getLocked())
			{
				productRepository.save(product);
				auditService.addAuditEntry(AuditActionTypeEnum.PRODUCT_PUBLISH, "Published " + product.getName() + " for Site: " + product.getSite().getSiteName(), "");
			}
			else
				log.warn("Locked product not published: " + product.getName());
		}

        productRepository.save(product);

		return product;
	}

    @Transactional
    public void requestPublishProduct(int productId){
        generationService.publishProductForId(productId);
    }

    @Transactional
    public ProductTagListEntry grabTag(Product product){
        ProductTagListEntry tagEntry = new ProductTagListEntry();

        if (product.isArchived()){
            return null;
        }

        if (!product.getLocked()) // Generation service checks if locked, but let's avoid unecessary warnings; only publish when unlocked
        {
            setWidgetId(product);
            requestPublishProduct(product.getId());
        }


        tagEntry.setProductId(product.getId());
        String tag = generationService.generateTag(product);
        tagEntry.setTag(tag);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = df.format(new Date());

        String fileName = "" + product.getSite().getPublisher().getName() + "__" + product.getSite().getSiteName() + "__" + product.getName() + "__id" + product.getId() + "__widgetid" + product.getWidgetId() + "__" + dateString + ".txt";
        fileName = fileName.replace(' ', '_');
        tagEntry.setFileName(fileName);

        return tagEntry;
    }

    @Transactional
	public ProductTagListEntry grabTag(Integer productId, UserModel user) throws Exception {
		Product product = getProductEntityById(productId, user);
        return grabTag(product);
	}

	@Transactional
	public ProductTagListModel grabTags(List<Integer> productTagIds, UserModel user) throws Exception
	{

		ProductTagListModel tagList = new ProductTagListModel();

		for (Integer productTagId : productTagIds)
		{
			if (!getProductById(productTagId, user).isDefault())
			{
				ProductTagListEntry tagListEntry = grabTag(productTagId, user);
				if (tagListEntry != null)
					tagList.addTag(tagListEntry);
			}
		}

		return tagList;
	}

	@Transactional
	public ProductTagListModel grabTagsWithDefaults(List<Integer> productTagIds, UserModel user) throws Exception
	{

		ProductTagListModel tagList = new ProductTagListModel();

		for (Integer productTagId : productTagIds)
		{
			ProductTagListEntry tagListEntry = grabTag(productTagId, user);
			if (tagListEntry != null)
				tagList.addTag(tagListEntry);
		}

		return tagList;
	}


	public void createTagZipResponse(ProductTagListModel tags, HttpServletResponse response) throws Exception
	{
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

		ZipOutputStream zipOutputStream = new ZipOutputStream(outputBuffer);
		zipOutputStream.setLevel(ZipOutputStream.STORED); // No need to compress something so small :-)

		for (int i = 0; i < tags.getTags().size(); i++)
		{
			ZipArchiveEntry zipEntry = new ZipArchiveEntry(tags.getTags().get(i).getFileName());
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(tags.getTags().get(i).getTag().getBytes());
			zipOutputStream.closeEntry();
		}

		zipOutputStream.finish();
		response.getOutputStream().write(outputBuffer.toByteArray());
		response.getOutputStream().flush();
		outputBuffer.close();

		return;

	}

	public String createTagTextResponse(ProductTagListModel tags)
	{
		String allTags = "";

		for (int i = 0; i < tags.getTags().size(); i++)
		{
			allTags += tags.getTags().get(i).getTag() + "\r\n\r\n";
			if (i!= tags.getTags().size() - 1)
				allTags += "<!-- ******************************************************************** -->\r\n\r\n";
		}

		return allTags;

	}

	/**
	 * AdUnit passback related methods. See ProductController for more info
	 */


	@Transactional
	public Iterable<AdUnit> getAdUnitsByProduct(ProductModel productModel, UserModel userModel)
	{
		return adUnitRepository.findAllByProductId(productModel.getId());
	}

	@Transactional
	public AdUnitListModel getAdUnitsListModelByProduct(ProductModel productModel, UserModel userModel)
	{
		AdUnitListModel unitModels = new AdUnitListModel();
		Iterable<AdUnit> units = getAdUnitsByProduct(productModel, userModel);

		for (AdUnit unit : units)
		{
			AdUnitModel unitModel = new AdUnitModel();
			unitModel.fromAdUnitAndProduct(unit,productModel);
			unitModels.add(unitModel);
		}

		return unitModels;
	}

	@Transactional
	public AdUnit getAdUnitFromAdUnitModel(AdUnitModel adUnitModel, Product product)
	{
		AdUnit unit = new AdUnit();
		unit.fromAdUnitModel(adUnitModel, product);
		return unit;
	}


	@Transactional
	public List<AdUnit> convertAdUnitListModelToAdUnitList(List<AdUnitModel> adUnitModels)
	{
		List<AdUnit> adUnitList = new ArrayList<>();

		for (AdUnitModel adUnitModel : adUnitModels)
		{
			adUnitList.add(getAdUnitFromAdUnitModel(adUnitModel, productRepository.findOne(adUnitModel.getProductId())));
		}

		return adUnitList;
	}

	@Transactional
	public AdUnitListModel convertAdUnitListToAdUnitListModel(ProductModel productModel, Iterable<AdUnit> adUnitList)
	{
		AdUnitListModel adUnitListModel = new AdUnitListModel();

		for (AdUnit unit : adUnitList)
		{
			AdUnitModel aModel = new AdUnitModel();
			aModel.fromAdUnitAndProduct(unit,productModel);
			adUnitListModel.add(aModel);
		}

		return adUnitListModel;
	}

	@Transactional
	public Iterable<AdUnit> createPassbackAdUnits(Iterable<AdUnit> adUnits, ProductModel productModel, UserModel userModel)
	{
		List<AdUnit> saved = new ArrayList<AdUnit>();
		for (AdUnit item : adUnits)
		{
			if (!isExistingAdUnit(item))
			{
				item.setPlatformCreated(false); // The only way ad units would be created after the product is created is if it's a passback unit
				adUnitRepository.save(item);
				saved.add(item);
			}
		}
		return saved;
	}

	@Transactional
	public void deletePassbackAdUnits(AdUnit adUnit, ProductModel productModel, UserModel userModel)
	{
		if (isExistingAdUnit(adUnit) && !isPlatformCreated(adUnit))  // Avoid deleting non-passback unit
		{
			adUnitRepository.delete(adUnit);
		}
		else
		{
			throw new ModelValidationException("Ad unit id is not valid id or is platform-required.");
		}
		return;
	}

	@Transactional
	public Iterable<AdUnit> updatePassbackAdUnits(Iterable<AdUnit> adUnits, ProductModel productModel, UserModel userModel)
	{

		Product product;
		try
		{
			product = productRepository.findOne(productModel.getId());
		}
		catch (Exception exc)
		{
			log.error("Error updating product", exc);
			throw new ServiceException(exc.getMessage());
		}
		if (product.getLocked() == true)
		{
			log.warn("Not updating product adunits for " + product.getName() + " ("+ product.getId() + ") because it is locked.");
			return adUnits;
		}

		List<AdUnit> saved = new ArrayList<>();

		for (AdUnit item : adUnits)
		{
			if (!isPlatformCreated(item))  // Avoid updating non-passback unit
			{
                item.setAdServerType(AdServerType.OPEN_X);
				item.setPlatformCreated(false); // The only way ad units would be created after the product is created is if it's a passback unit
				adUnitRepository.save(item);
				saved.add(item);
			}
		}

		return saved;
	}

	private Boolean isExistingAdUnit(AdUnit adUnit)
	{
		if (adUnit.getId() != 0)
		{
			if (null == getAdUnitById(adUnit.getId()))
				return false;
			else
				return true;
		}
		else
			return false;
	}

	private Boolean isPlatformCreated(AdUnit adUnit)
	{
		if (isExistingAdUnit(adUnit))
		{
			// Don't trust the data sent in adUnit, it could have been set in json by api caller
			AdUnit unit = getAdUnitById(adUnit.getId());

            if (null == unit){
                return false;
            }

            return unit.isPlatformCreated();
		} else {
            return false;
        }

	}

	@Transactional
	public AdUnit getAdUnitById(int id)
	{
		return adUnitRepository.findOne(id);
	}
}
