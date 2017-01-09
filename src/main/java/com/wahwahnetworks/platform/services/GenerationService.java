package com.wahwahnetworks.platform.services;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.*;
import com.wahwahnetworks.platform.data.repos.AdUnitRepository;
import com.wahwahnetworks.platform.data.repos.ProductRepository;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.lib.JacksonSerializer;
import com.wahwahnetworks.platform.lib.JsonSerializer;
import com.wahwahnetworks.platform.models.generation.*;
import com.wahwahnetworks.platform.models.web.ClientFeatureWebModel;
import com.wahwahnetworks.platform.models.web.ProductClientFeatureModel;
import com.wahwahnetworks.platform.services.tie.TagIntelligenceNetworkExportService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Justin on 2/4/2015.
 */

@Service
public class GenerationService
{
	private static final Logger logger = Logger.getLogger(GenerationService.class);


	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private FilePublisherService filePublisherService;

	@Autowired
	private PlatformService platformService;

    @Autowired
    private AdUnitRepository adUnitRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TagIntelligenceNetworkExportService tagIntelligenceNetworkExportService;

	@Autowired
	Environment environment;

	final String TAGINTELLIGENCE_DEV = "http://localhost:8080/tagintelligence/";
	final String TAGINTELLIGENCE_STAGING = "http://cdn-tie.dev.wwnstatic.com/tagintelligence/"; // no-cache alias tie.dev.wahwahnetworks.com
	//final String TAGINTELLIGENCE_STAGING = "http://tie.dev.wahwahnetworks.com/tagintelligence/";
	final String TAGINTELLIGENCE_PRODUCTION =  "http://cdn-tie.wwnstatic.com/tagintelligence/";

	private String getEnvironment()
	{
		if (environment.equals(Environment.PRODUCTION)){
            return "prod";
        }
		else if (environment.equals(Environment.STAGING)){
            return "qa";
        } else {
            return "dev";
        }
	}

	@Transactional(readOnly = true)
	public void publishProductForId(int productId) {
        try {

            logger.info("Start Publishing: " + productId);

            Product product = productRepository.findOne(productId);
            publishProduct(product);

			List<LineItem> lineItemsPublished = new ArrayList<>();
            tagIntelligenceNetworkExportService.exportWidgetsForProduct(product,lineItemsPublished);
        } catch (Exception exception){
            logger.error(exception);
        }
	}

    @Transactional(readOnly = true)
	public void publishProduct(Product product) throws Exception
	{

		if (product.getLocked())
		{
			logger.warn("Did not publish locked product: " + product.getName() + " (" + product.getId() + ")");
			return;
		}

		if (product.isArchived())
		{
			logger.warn("Did not publish locked product: " + product.getName() + " (" + product.getId() + ")");
			return;
		}

		String generatedCode = generateProduct(product);

		String path = "product/publishers/_release/"+getEnvironment()+"/" + product.getWidgetId() + "/wahwahobject.js";
		String purgePath = "product/publishers/_release/"+getEnvironment()+"/" + product.getWidgetId() + "/*";

		FTPClient ftpClient = filePublisherService.getEdgeCastFTPClient();
		filePublisherService.uploadFileToEdgeCast(ftpClient, path, generatedCode.getBytes());
		ftpClient.disconnect();

		filePublisherService.purgeFileOnEdgeCast(purgePath);

		publishLegacyProductRedirect(product);
	}

	private void publishLegacyProductRedirect(Product product) throws Exception
	{

		String legacyPath = "toolbar/publishers/" + product.getWidgetId() + "/wahwahobject.js";
		String legacyPurgePath = "private/toolbar/publishers/" + product.getWidgetId() + "/*";
		String legacyUrl = "http://cdn-s.wahwahnetworks.com/00BA6A/toolbar/publishers/" + product.getWidgetId() + "/wahwahobject.js";
		String newUrl = "http://cdn-s.wwnstatic.com/00BA6A/product/publishers/_release/"+getEnvironment()+"/" + product.getWidgetId() + "/wahwahobject.js";

		Boolean needsRedirect = false;

		URL legacyUrlObject = new URL(legacyUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) legacyUrlObject.openConnection();

		if (httpURLConnection.getResponseCode() == 200)
		{
			needsRedirect = true;
		}

		if (needsRedirect)
		{
			String redirectScript = "var wwlegacyurl=\"" + newUrl + "\";\n" +
					"if (document.readyState === \"complete\" || document.readyState === \"interactive\") \n" +
					"{\n" +
					"\t// In this case, loader.js will use DOM manipulations\n" +
					"\tvar fileref=document.createElement('script');\n" +
					"\tfileref.setAttribute(\"type\",\"text/javascript\");\n" +
					"\tfileref.setAttribute(\"src\", wwlegacyurl);\n" +
					"\tdocument.getElementsByTagName(\"head\")[0].appendChild(fileref);\n" +
					"}\n" +
					"else\n" +
					"{\n" +
					"\tdocument.writeln('<scr'+'ipt src=\"'+wwlegacyurl+'\" type=\"text/javascript\"></scr'+'ipt>');\n" +
					"}";

			FTPClient ftpClient = filePublisherService.getEdgeCastFTPClient();
			filePublisherService.uploadFileToEdgeCast(ftpClient, legacyPath, redirectScript.getBytes());
			ftpClient.disconnect();

			filePublisherService.purgeFileOnEdgeCast(legacyPurgePath);
		}
	}

	@Transactional(readOnly = true)
	public String generateProduct(Product product)
	{
		ProductType productType = product.getProductType();

		String generatedCode = null;

		if (productType == ProductType.TOOLBAR)
		{
			generatedCode = generateToolbar(product);
		}

		if (productType == ProductType.STANDALONE_AD)
		{
			generatedCode = generateAdOnly(product);
		}

		if (productType == ProductType.CUSTOM)
		{
			generatedCode = generateCustom(product);
		}

		if (productType == ProductType.AD_SERVER_NATIVE)
		{
			generatedCode = generateAdServerNative(product);
		}

		logger.info("Generated Source Code:\r\n\r\n" + generatedCode);

		return generatedCode;
	}

	@Transactional(readOnly = true)
	private String generateAdOnly(Product product)
	{
		GenerationToolbarModel toolbarModel = new GenerationToolbarModel();
		toolbarModel.setId(product.getWidgetId());
		toolbarModel.setPublishDate(dateAsString());
		toolbarModel.setEnvironment(getEnvironment());
		toolbarModel.setApplication("adOnly");
		toolbarModel.setPlatformVersion(platformService.getPlatformVersion());
		toolbarModel.setPlatformCommitId(platformService.getCommitId());
		toolbarModel.setBranch(product.getProductVersion().getGitBranchName());
		toolbarModel.setVersion(product.getProductVersion().getVersionName());
		toolbarModel.setLogLevel(product.getLogLevel().toString());
		toolbarModel.setLoadPreference("ALWAYS");
		toolbarModel.setDemandSourceListUrl(getDemandSourceUrl(product.getWidgetId()));

		setClientFeatures(product,toolbarModel);

		boolean isDebug = product.getIsDebug();

		if (isDebug)
		{
			toolbarModel.setDebugMode("true");
		}
		else
		{
			toolbarModel.setDebugMode("false");
		}

		// Info comments
		toolbarModel.setProductId(product.getId());
		toolbarModel.setProductName(product.getName());
		toolbarModel.setSiteName(product.getSite().getSiteName());

		toolbarModel.setPosition("bottom");

		Language language = product.getSite().getLanguage();

		switch (language)
		{
			case en:
				toolbarModel.setLanguage("EN");
				break;
			case es:
				toolbarModel.setLanguage("ES");
				break;
			case pt:
				toolbarModel.setLanguage("PT");
				break;
		}

		toolbarModel.setSkin("default");

		GenerationAdConfigModel adConfig = new GenerationAdConfigModel();
		GenerationAdConfigInPageModel inPage = new GenerationAdConfigInPageModel();
		adConfig.setInPage(inPage);
		toolbarModel.setAdConfig(adConfig);

		inPage.setCombinedDisplayURL(getAdURIHelper(product.getAdConfigInPage().getPrimaryDisplayAdUnit()));
		inPage.setBackupDisplayURL(getAdURIHelper(product.getAdConfigInPage().getBackupDisplayAdUnit()));
		inPage.setMobile320URL(getAdURIHelper(product.getAdConfigInPage().getMobileAdUnit()));
		inPage.setMobile728URL(getAdURIHelper(product.getAdConfigInPage().getTabletAdUnit()));
		inPage.setBackupVideoURL(getAdURIHelper(product.getAdConfigInPage().getVideoAdUnit()));
		inPage.setLeaveBehindURL(getAdURIHelper(product.getAdConfigInPage().getLeaveBehindAdUnit()));

		inPage.setDisplayWidth(product.getAdConfigInPage().getDisplayWidth());
		inPage.setDisplayHeight(product.getAdConfigInPage().getDisplayHeight());
		inPage.setVideoWidth(product.getAdConfigInPage().getVideoWidth());
		inPage.setVideoHeight(product.getAdConfigInPage().getVideoHeight());
		inPage.setAudioVolume(product.getAdConfigInPage().getAudioVolume());

		inPage.setInAdBreakout(product.getProductStandaloneAd().getBreakoutAd());
		inPage.setOutstreamAutoload(product.getProductStandaloneAd().isOutstreamAutoload());
		inPage.setOutstreamTriggerId(product.getProductStandaloneAd().getOutstreamTriggerId());
		inPage.setOutstreamFloat(product.getProductStandaloneAd().isOutstreamFloat());

		inPage.setPassbackHTML(null);
		inPage.setPassbackSource("not set anywhere (this is ok)");
		try
		{
			if (product.getSite().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("site");
			}
			else if (product.getSite().getPublisher().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPublisher().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("publisher");
			}
			else if (product.getSite().getPublisher().getParentAccount() != null && product.getSite().getPublisher().getParentAccount().getAccountType() == AccountType.NETWORK)
			{
				AccountNetwork acc = (AccountNetwork) product.getSite().getPublisher().getParentAccount();
				if (acc.getPassbackDisplayTagHtml() != null)
				{
					inPage.setPassbackHTML(JsonSerializer.serialize(acc.getPassbackDisplayTagHtml()));
					inPage.setPassbackSource("network");
				}
			}
		}
		catch(Exception e)
		{
			inPage.setPassbackHTML(null);
			inPage.setPassbackSource("JsonSerializer error");
		}

		AlignHorizontal expansionHorizontalAlignment = product.getProductStandaloneAd().getExpansionAlignHorizontal();
		AlignVertical expansionVerticalAlignment = product.getProductStandaloneAd().getExpansionAlignVertical();

		StandaloneAdFormat adFormat = product.getProductStandaloneAd().getAdFormat();

		switch (adFormat)
		{
			case floater:
				inPage.setAdType("floater");
				break;
			case banner:
				inPage.setAdType("banner");
				break;
			case ostream:
				inPage.setAdType("ostream");
				break;
		}

		switch (expansionVerticalAlignment)
		{
			case top:
				inPage.setExpansionAlignVert("top");
				break;
			case middle:
				inPage.setExpansionAlignVert("middle");
				break;
			case bottom:
				inPage.setExpansionAlignVert("bottom");
				break;
		}

		switch (expansionHorizontalAlignment)
		{
			case left:
				inPage.setExpansionAlignHoriz("left");
				break;
			case center:
				inPage.setExpansionAlignHoriz("center");
				break;
			case right:
				inPage.setExpansionAlignHoriz("right");
				break;
		}

		if (adFormat == StandaloneAdFormat.floater)
		{
			inPage.setHasFloatingAd(true);

			AlignVertical floatVerticalAlignment = product.getProductStandaloneAd().getAlignVertical();
			AlignHorizontal floatHorizontalAlignment = product.getProductStandaloneAd().getAlignHorizontal();

			switch (floatVerticalAlignment)
			{
				case top:
					inPage.setFloatVerticalPosition("top");
					break;
				case middle:
					inPage.setFloatVerticalPosition("middle");
					break;
				case bottom:
					inPage.setFloatVerticalPosition("bottom");
					break;
			}

			switch (floatHorizontalAlignment)
			{
				case left:
					inPage.setFloatHorizontalPosition("left");
					break;
				case center:
					inPage.setFloatHorizontalPosition("center");
					break;
				case right:
					inPage.setFloatHorizontalPosition("right");
					break;
			}

		}
		else
		{
			inPage.setFloatVerticalPosition("bottom");
			inPage.setFloatHorizontalPosition("right");
			inPage.setHasFloatingAd(false);
		}

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("toolbar", toolbarModel);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_object.js.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		return stringWriter.toString();
	}

	@Transactional(readOnly = true)
    private String generateAdServerNative(Product product){
        Iterable<AdUnit> adUnits = adUnitRepository.findAllByProductId(product.getId());

        for(AdUnit adUnit : adUnits){
            // There will only ever be one...

            GenerationAdServerNativeModel nativeModel = new GenerationAdServerNativeModel();
            nativeModel.setId(product.getWidgetId());
            nativeModel.setProductId(product.getId());
            nativeModel.setProductName(product.getName());
            nativeModel.setPublishDate(dateAsString());
            nativeModel.setSiteName(product.getSite().getSiteName());

            nativeModel.setPlatformVersion(platformService.getPlatformVersion());
            nativeModel.setPlatformCommitId(platformService.getCommitId());

            if(adUnit.getAdServerType() == AdServerType.OPEN_X){
                Integer adUnitId = Integer.parseInt(adUnit.getAdServerUnitId());
            }

            if(nativeModel.getNativeTagContents() == null){
                return "";
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("config", nativeModel);

            Template template = velocityEngine.getTemplate("code_templates/adserver_redirect.js.vm");

            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);

            return stringWriter.toString();
        }

        return "";
    }

	private String getAdURIHelper(AdUnit unit)
	{
		if (unit == null)
			return "";
		if (unit.getAdServerType() == AdServerType.OPEN_X)
		{
			if (unit.getAdUnitType().equals(AdUnitType.VIDEO))
			{
				return "http://ox-d.wahwahnetworks.com/v/1.0/av?auid=" + unit.getAdServerUnitId();
			}
			else
			{
				return "ww-openx:auid=" + unit.getAdServerUnitId() + ";";
			}
		}
		else if (unit.getAdServerType() == AdServerType.GENERIC_URL)
		{
			return unit.getAdServerUnitId();
		}
		else return ""; // Shouldn't get here
	}

	@Transactional(readOnly = true)
	private void setClientFeatures(Product product, GenerationToolbarModel toolbarModel){
		Map<Integer,ClientFeatureWebModel> clientFeatureWebModelMap = new HashMap<>();
		Map<String,ProductClientFeatureModel> productClientFeatureModelMap = new HashMap<>();

		for (ProductClientFeature productClientFeature : product.getFeatures()){
			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());

			productClientFeatureModelMap.put(productClientFeature.getClientFeature().getClientVariableName(),productClientFeatureModel);

			ClientFeature clientFeature = productClientFeature.getClientFeature();

			ClientFeatureWebModel clientFeatureWebModel = new ClientFeatureWebModel();
			clientFeatureWebModel.setId(clientFeature.getId());
			clientFeatureWebModel.setName(clientFeature.getName());
			clientFeatureWebModel.setDescription(clientFeature.getDescription());
			clientFeatureWebModel.setVariableName(clientFeature.getClientVariableName());
			clientFeatureWebModel.setVariableLabel(clientFeature.getClientVariableLabel());
			clientFeatureWebModel.setVariableType(clientFeature.getClientVariableType());

			clientFeatureWebModelMap.put(clientFeature.getId(),clientFeatureWebModel);
		}

		toolbarModel.setProductClientFeatures(productClientFeatureModelMap);
		toolbarModel.setClientFeaturesModelMap(clientFeatureWebModelMap);
	}


	private String generateToolbar(Product product)
	{
		GenerationToolbarModel toolbarModel = new GenerationToolbarModel();
		toolbarModel.setId(product.getWidgetId());
		toolbarModel.setPublishDate(dateAsString());
		toolbarModel.setEnvironment(getEnvironment());
		toolbarModel.setApplication("radio");
		toolbarModel.setPlatformVersion(platformService.getPlatformVersion());
		toolbarModel.setPlatformCommitId(platformService.getCommitId());
		toolbarModel.setBranch(product.getProductVersion().getGitBranchName());
		toolbarModel.setVersion(product.getProductVersion().getVersionName());
		toolbarModel.setLogLevel(product.getLogLevel().toString());
		toolbarModel.setLoadPreference(product.getProductToolbar().getLoadPreference().toString());
		toolbarModel.setDemandSourceListUrl(getDemandSourceUrl(product.getWidgetId()));

		setClientFeatures(product,toolbarModel);

		boolean isDebug = product.getIsDebug();

		if (isDebug)
		{
			toolbarModel.setDebugMode("true");
		}
		else
		{
			toolbarModel.setDebugMode("false");
		}

		// Info comments
		toolbarModel.setProductId(product.getId());
		toolbarModel.setProductName(product.getName());
		toolbarModel.setSiteName(product.getSite().getSiteName());

		RadioAlign radioAlign = product.getProductToolbar().getRadioAlign();

		switch (radioAlign)
		{ // Value in JS is inverted from value stored
			case left:
				toolbarModel.setPosition("right");
				break;
			case right:
				toolbarModel.setPosition("left");
				break;
		}

		Language language = product.getSite().getLanguage();

		switch (language)
		{
			case en:
				toolbarModel.setLanguage("EN");
				break;
			case es:
				toolbarModel.setLanguage("ES");
				break;
			case pt:
				toolbarModel.setLanguage("PT");
				break;
		}

		SkinType skinType = product.getProductToolbar().getSkinType();

		if (skinType != null)
		{
			switch (skinType)
			{
				case dark:
					toolbarModel.setSkin("dark");
					break;
				case light:
					toolbarModel.setSkin("light");
					break;
				case custom:
					toolbarModel.setSkin("custom");
					break;
			}
		}
		else
		{
			toolbarModel.setSkin("default");
		}

		GenerationToolbarRadioModel radioModel = new GenerationToolbarRadioModel();
		radioModel.setType("tab"); // Set default mode to Popup ('tab'). Inline ('inline') is also supported

		toolbarModel.setRadio(radioModel);

		GenerationAdConfigModel adConfig = new GenerationAdConfigModel();
		GenerationAdConfigInPageModel inPage = new GenerationAdConfigInPageModel();
		adConfig.setInPage(inPage);
		toolbarModel.setAdConfig(adConfig);


		inPage.setPassbackHTML(null);
		inPage.setPassbackSource("not set anywhere (this is ok)");
		try
		{
			if (product.getSite().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("site");
			}
			else if (product.getSite().getPublisher().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPublisher().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("publisher");
			}
			else if (product.getSite().getPublisher().getParentAccount() != null && product.getSite().getPublisher().getParentAccount().getAccountType() == AccountType.NETWORK)
			{
				AccountNetwork acc = (AccountNetwork) product.getSite().getPublisher().getParentAccount();
				if (acc.getPassbackDisplayTagHtml() != null)
				{
					inPage.setPassbackHTML(JsonSerializer.serialize(acc.getPassbackDisplayTagHtml()));
					inPage.setPassbackSource("network");
				}
			}
		}
		catch(Exception e)
		{
			inPage.setPassbackHTML(null);
			inPage.setPassbackSource("JsonSerializer error");
		}

		inPage.setCombinedDisplayURL(getAdURIHelper(product.getAdConfigInPage().getPrimaryDisplayAdUnit()));
		inPage.setBackupDisplayURL(getAdURIHelper(product.getAdConfigInPage().getBackupDisplayAdUnit()));
		inPage.setMobile320URL(getAdURIHelper(product.getAdConfigInPage().getMobileAdUnit()));
		inPage.setMobile728URL(getAdURIHelper(product.getAdConfigInPage().getTabletAdUnit()));
		inPage.setBackupVideoURL(getAdURIHelper(product.getAdConfigInPage().getVideoAdUnit()));
		inPage.setLeaveBehindURL(getAdURIHelper(product.getAdConfigInPage().getLeaveBehindAdUnit()));

		inPage.setDisplayWidth(product.getAdConfigInPage().getDisplayWidth());
		inPage.setDisplayHeight(product.getAdConfigInPage().getDisplayHeight());
		inPage.setVideoWidth(product.getAdConfigInPage().getVideoWidth());
		inPage.setVideoHeight(product.getAdConfigInPage().getVideoHeight());
		inPage.setAudioVolume(product.getAdConfigInPage().getAudioVolume());

		inPage.setHasFloatingAd(false);
		inPage.setFloatVerticalPosition("bottom");
		inPage.setFloatHorizontalPosition("right");

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("toolbar", toolbarModel);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_object.js.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		return stringWriter.toString();
	}


	private String generateCustom(Product product)
	{
		GenerationToolbarModel toolbarModel = new GenerationToolbarModel();
		toolbarModel.setId(product.getWidgetId());
		toolbarModel.setPublishDate(dateAsString());
		toolbarModel.setEnvironment(getEnvironment());
		toolbarModel.setApplication("custom");
		toolbarModel.setPlatformVersion(platformService.getPlatformVersion());
		toolbarModel.setPlatformCommitId(platformService.getCommitId());
		toolbarModel.setBranch(product.getProductVersion().getGitBranchName());
		toolbarModel.setVersion(product.getProductVersion().getVersionName());
		toolbarModel.setLogLevel(product.getLogLevel().toString());
		toolbarModel.setLoadPreference(product.getProductToolbar().getLoadPreference().toString());
		toolbarModel.setDemandSourceListUrl(getDemandSourceUrl(product.getWidgetId()));

		setClientFeatures(product,toolbarModel);

		toolbarModel.setDebugMode("true"); // Always debug for custom so src can be edited

		// Info comments
		toolbarModel.setProductId(product.getId());
		toolbarModel.setProductName(product.getName());
		toolbarModel.setSiteName(product.getSite().getSiteName());

		toolbarModel.setPosition("left");

		toolbarModel.setLanguage("EN");

		toolbarModel.setSkin("dark");

		GenerationToolbarRadioModel radioModel = new GenerationToolbarRadioModel();
		radioModel.setType("tab");

		toolbarModel.setRadio(radioModel);

		GenerationAdConfigModel adConfig = new GenerationAdConfigModel();
		GenerationAdConfigInPageModel inPage = new GenerationAdConfigInPageModel();
		adConfig.setInPage(inPage);
		toolbarModel.setAdConfig(adConfig);

		inPage.setCombinedDisplayURL("");
		inPage.setBackupDisplayURL("");
		inPage.setMobile320URL("");
		inPage.setMobile728URL("");
		inPage.setBackupVideoURL("");
		inPage.setLeaveBehindURL("");

		inPage.setDisplayWidth(300);
		inPage.setDisplayHeight(250);
		inPage.setVideoWidth(558);
		inPage.setVideoHeight(314);

		inPage.setHasFloatingAd(false);
		inPage.setFloatVerticalPosition("bottom");
		inPage.setFloatHorizontalPosition("right");

		inPage.setPassbackHTML(null);
		inPage.setPassbackSource("not set anywhere (this is ok)");
		try
		{
			if (product.getSite().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("site");
			}
			else if (product.getSite().getPublisher().getPassbackDisplayTagHtml() != null)
			{
				inPage.setPassbackHTML(JsonSerializer.serialize(product.getSite().getPublisher().getPassbackDisplayTagHtml()));
				inPage.setPassbackSource("publisher");
			}
			else if (product.getSite().getPublisher().getParentAccount() != null && product.getSite().getPublisher().getParentAccount().getAccountType() == AccountType.NETWORK)
			{
				AccountNetwork acc = (AccountNetwork) product.getSite().getPublisher().getParentAccount();
				if (acc.getPassbackDisplayTagHtml() != null)
				{
					inPage.setPassbackHTML(JsonSerializer.serialize(acc.getPassbackDisplayTagHtml()));
					inPage.setPassbackSource("network");
				}
			}
		}
		catch(Exception e)
		{
			inPage.setPassbackHTML(null);
			inPage.setPassbackSource("JsonSerializer error");
		}

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("toolbar", toolbarModel);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_object.js.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		return stringWriter.toString();
	}



	@Transactional(readOnly = true)
	public String generateTag(Product product)
	{
		if (product.getWidgetId() == null)
		{
			throw new ServiceException(product.getName() + " (id: " + product.getId() + ") has not yet been published!");
		}

		GenerationTagModel tag = new GenerationTagModel();
		tag.setProductName(product.getName());
		tag.setProductId(product.getId());
		tag.setSiteName(product.getSite().getSiteName());
		tag.setWidgetId(product.getWidgetId());
		tag.setInstructionsPagePosition("HEAD of your page");

		Map<String,ProductClientFeatureModel> productClientFeatureModelMap = new HashMap<>();

		for (ProductClientFeature productClientFeature : product.getFeatures()){
			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());

			productClientFeatureModelMap.put(productClientFeature.getClientFeature().getClientVariableName(),productClientFeatureModel);
		}

		tag.setClientFeatures(productClientFeatureModelMap);

		if (product.getProductType() == ProductType.STANDALONE_AD)
		{

			if (product.getProductStandaloneAd().getAdFormat() == StandaloneAdFormat.banner)
			{
				tag.setProductTypeDescription("Banner");
				int width = product.getAdConfigInPage().getDisplayWidth();
				int height = product.getAdConfigInPage().getDisplayHeight();
				tag.setInstructionsPagePosition("desired " + width + "x" + height + " banner location.");
				tag.setInstructionsPagePosition("HEAD of your page");
			}
			else if (product.getProductStandaloneAd().getAdFormat() == StandaloneAdFormat.floater)
			{
				tag.setProductTypeDescription("Floater");
				tag.setInstructionsPagePosition("HEAD of your page or 1x1 placement");
			}
			else
			{
				tag.setProductTypeDescription("Unknown");
			}

		}
		else
		{
			tag.setProductTypeDescription("Bar");
		}

		tag.setPublisherName(product.getSite().getPublisher().getName());
		tag.setObjectPath("http://cdn-s.wwnstatic.com/00BA6A/product/publishers/_release/" + getEnvironment() + "/" + product.getWidgetId() + "/wahwahobject.js");

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("tag", tag);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_tag.html.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		String tagString = stringWriter.toString();

		// make dos format
		tagString = tagString.replace("\r\n","\n"); // First make identical on windows and linux
		tagString = tagString.replace("\n","\r\n"); // First make identical on windows and linux

		return tagString;
	}

	protected String getTagIntelligencePrefix()
	{
		String prefix = TAGINTELLIGENCE_DEV + "tie";

		if (environment.equals(Environment.PRODUCTION))
		{
			prefix = TAGINTELLIGENCE_PRODUCTION + "tie";
		}
		else if (environment.equals(Environment.STAGING))
		{
			prefix = TAGINTELLIGENCE_STAGING + "tie";
		}

		return prefix;
	}


	protected String getDemandSourceUrl(Integer widgetId)
	{
		String url = TAGINTELLIGENCE_DEV + "serve/" + widgetId;

		if (environment.equals(Environment.PRODUCTION))
		{
			url = TAGINTELLIGENCE_PRODUCTION + "serve/" + widgetId;
		}
		else if (environment.equals(Environment.STAGING))
		{
			url = TAGINTELLIGENCE_STAGING + "serve/" + widgetId;
		}

		return url;
	}

	// Todo: Lots of duplicated code between the three generateUniversalTags
	public String generateUniversalPublisherTag(AccountPublisher publisher)
	{

		GenerationTagModel tag = new GenerationTagModel();
		tag.setProductTypeDescription("Universal");
		tag.setInstructionsPagePosition("Desired placement: HEAD of your page, banner or 1x1 placement");
/*

Todo: See PLATFORM-447

		Map<String,ProductClientFeatureModel> productClientFeatureModelMap = new HashMap<>();

		for (ProductClientFeature productClientFeature : product.getFeatures()){
			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());

			productClientFeatureModelMap.put(productClientFeature.getClientFeature().getClientVariableName(),productClientFeatureModel);
		}

		tag.setClientFeatures(productClientFeatureModelMap);

		*/

		tag.setSiteName("");
		tag.setNetworkName("");
		if (publisher.getDefaultProductFormat() != null)
		{
			tag.setDefaultTagFormat(publisher.getDefaultProductFormat().toString());
		}
		else if (publisher.getParentAccount() != null)
		{
			tag.setDefaultTagFormat(((AccountPublisher)publisher.getParentAccount()).getDefaultProductFormat().toString());
		}
		else // Shouldn't happen
		{
			tag.setDefaultTagFormat(ProductFormat.OUTSTREAM.toString());
		}

		if (publisher.getParentAccount() != null)
			tag.setNetworkName(publisher.getParentAccount().getName());
		tag.setPublisherName(publisher.getName());

		String prefix = getTagIntelligencePrefix();

		tag.setObjectPath(prefix + "/js/publisher-"+publisher.getId()+"/domain-${DOMAIN}");
		tag.setObjectId(publisher.getId()); // id attribute in universal script tag

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("tag", tag);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_universal_tag.html.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		String tagString = stringWriter.toString();

		// make dos format
		tagString = tagString.replace("\r\n","\n"); // First make identical on windows and linux
		tagString = tagString.replace("\n","\r\n"); // First make identical on windows and linux

		return tagString;
	}

	public String generateUniversalSiteTag(Site site)
	{
		GenerationTagModel tag = new GenerationTagModel();
		tag.setProductTypeDescription("Universal");
		tag.setInstructionsPagePosition("Desired placement: HEAD of your page, banner or 1x1 placement");
/*

Todo: See PLATFORM-447

		Map<String,ProductClientFeatureModel> productClientFeatureModelMap = new HashMap<>();

		for (ProductClientFeature productClientFeature : product.getFeatures()){
			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());

			productClientFeatureModelMap.put(productClientFeature.getClientFeature().getClientVariableName(),productClientFeatureModel);
		}

		tag.setClientFeatures(productClientFeatureModelMap);

		*/


		tag.setPublisherName(site.getPublisher().getName());
		tag.setNetworkName("");

		if (site.getPublisher().getParentAccount() != null){
			tag.setNetworkName(site.getPublisher().getParentAccount().getName());
		}

		tag.setSiteName(site.getSiteName());

		if (site.getDefaultProductFormat() != null)
		{
			tag.setDefaultTagFormat(site.getDefaultProductFormat().toString());
		}
		else
		{
			AccountPublisher publisher = site.getPublisher();
			if (publisher.getDefaultProductFormat() != null)
			{
				tag.setDefaultTagFormat(publisher.getDefaultProductFormat().toString());
			}
			else if (publisher.getParentAccount() != null)
			{
				tag.setDefaultTagFormat(((AccountPublisher)publisher.getParentAccount()).getDefaultProductFormat().toString());
			}
			else // Shouldn't happen
			{
				tag.setDefaultTagFormat(ProductFormat.OUTSTREAM.toString());
			}
		}


		String prefix = getTagIntelligencePrefix();

		tag.setObjectPath(prefix + "/js/site-"+site.getId()+"/domain-${DOMAIN}");
		tag.setObjectId(site.getId());  // id attribute in universal script tag

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("tag", tag);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_universal_tag.html.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		String tagString = stringWriter.toString();

		// make dos format
		tagString = tagString.replace("\r\n","\n"); // First make identical on windows and linux
		tagString = tagString.replace("\n","\r\n"); // First make identical on windows and linux

		return tagString;
	}

	public String generateUniversalNetworkTag(AccountNetwork network)
	{

		GenerationTagModel tag = new GenerationTagModel();
		tag.setProductTypeDescription("Universal");
		tag.setInstructionsPagePosition("Desired placement: HEAD of your page, banner or 1x1 placement");
/*

Todo: See PLATFORM-447

		Map<String,ProductClientFeatureModel> productClientFeatureModelMap = new HashMap<>();

		for (ProductClientFeature productClientFeature : product.getFeatures()){
			ProductClientFeatureModel productClientFeatureModel = new ProductClientFeatureModel();
			productClientFeatureModel.setFeatureId(productClientFeature.getClientFeature().getId());
			productClientFeatureModel.setValueString(productClientFeature.getVariableValueString());
			productClientFeatureModel.setValueNumber(productClientFeature.getVariableValueNumber());
			productClientFeatureModel.setValueBoolean(productClientFeature.getVariableValueBoolean());

			productClientFeatureModelMap.put(productClientFeature.getClientFeature().getClientVariableName(),productClientFeatureModel);
		}

		tag.setClientFeatures(productClientFeatureModelMap);

		*/

		tag.setPublisherName("");
		tag.setSiteName("");
		tag.setNetworkName(network.getName());

		tag.setDefaultTagFormat(network.getDefaultProductFormat().toString());


		String prefix = getTagIntelligencePrefix();

		tag.setObjectPath(prefix + "/js/network-"+network.getId()+"/domain-${DOMAIN}");
		tag.setObjectId(network.getId()); // id attribute in universal script tag

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("tag", tag);

		Template template = velocityEngine.getTemplate("code_templates/wahwah_universal_tag.html.vm");

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);

		String tagString = stringWriter.toString();

		// make dos format
		tagString = tagString.replace("\r\n","\n"); // First make identical on windows and linux
		tagString = tagString.replace("\n","\r\n"); // First make identical on windows and linux

		return tagString;
	}

	private String dateAsString()
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}
}
