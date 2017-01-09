package com.wahwahnetworks.platform.models.generation;

import com.wahwahnetworks.platform.models.web.ClientFeatureWebModel;
import com.wahwahnetworks.platform.models.web.ProductClientFeatureModel;

import java.util.Map;

/**
 * Created by Justin on 2/4/2015.
 */
public class GenerationToolbarModel
{
	private int id;
	private String type;
	private String version;
	private String branch;
	private String environment;
	private String position;
	private String language;
	private String skin;
	private String application;
	private String debugMode;
	private String logLevel;
	private String platformVersion;
	private String platformCommitId;
	private String loadPreference;
	private String demandSourceListUrl;
	private GenerationToolbarRadioModel radio;
	private GenerationAdConfigModel adConfig;

	private Map<String,ProductClientFeatureModel> productClientFeatureModelMap;
	private Map<Integer,ClientFeatureWebModel> clientFeaturesModelMap;

	/* Info comments */
	private String productName;
	private String siteName;
	private Integer productId;
	private String publishDate;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getBranch()
	{
		return branch;
	}

	public void setBranch(String branch)
	{
		this.branch = branch;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getSkin()
	{
		return skin;
	}

	public void setSkin(String skin)
	{
		this.skin = skin;
	}

	public String getApplication()
	{
		return application;
	}

	public void setApplication(String application)
	{
		this.application = application;
	}

	public GenerationToolbarRadioModel getRadio()
	{
		return radio;
	}

	public void setRadio(GenerationToolbarRadioModel radio)
	{
		this.radio = radio;
	}

	public GenerationAdConfigModel getAdConfig()
	{
		return adConfig;
	}

	public void setAdConfig(GenerationAdConfigModel adConfig)
	{
		this.adConfig = adConfig;
	}


	public String getDebugMode()
	{
		return debugMode;
	}

	public void setDebugMode(String debugMode)
	{
		this.debugMode = debugMode;
	}

	public String getPlatformVersion()
	{
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion)
	{
		this.platformVersion = platformVersion;
	}

	public String getPlatformCommitId()
	{
		return platformCommitId;
	}

	public void setPlatformCommitId(String platformCommitId)
	{
		this.platformCommitId = platformCommitId;
	}

	public String getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(String logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getLoadPreference()
	{
		return loadPreference;
	}

	public void setLoadPreference(String loadPreference)
	{
		this.loadPreference = loadPreference;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getPublishDate()
	{
		return publishDate;
	}

	public void setPublishDate(String publishDate)
	{
		this.publishDate = publishDate;
	}

	/* Info comments */
	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public Map<String, ProductClientFeatureModel> getProductClientFeatures() {
		return productClientFeatureModelMap;
	}

	public void setProductClientFeatures(Map<String, ProductClientFeatureModel> clientFeatureModelMap) {
		this.productClientFeatureModelMap = clientFeatureModelMap;
	}

	public Map<Integer, ClientFeatureWebModel> getClientFeatures(){
		return clientFeaturesModelMap;
	}

	public void setClientFeaturesModelMap(Map<Integer, ClientFeatureWebModel> clientFeaturesModelMap){
		this.clientFeaturesModelMap = clientFeaturesModelMap;
	}

	public String getDemandSourceListUrl()
	{
		return demandSourceListUrl;
	}

	public void setDemandSourceListUrl(String demandSourceListUrl)
	{
		this.demandSourceListUrl = demandSourceListUrl;
	}
}
