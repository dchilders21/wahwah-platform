package com.wahwahnetworks.platform.models.generation;

/**
 * Created by Justin on 2/5/2015.
 */
public class GenerationAdConfigInPageModel
{
	private boolean enabled;
	private int adSize;
	private int videoWidth;
	private int videoHeight;
	private int displayWidth;
	private int displayHeight;
	private int bannerAdsPerVideoAd;
	private boolean frequencyCap;
	private int frequencyNumber;
	private String combinedDisplayURL;
	private String combinedDisplayLBURL;
	private String backupDisplayURL;
	private String mobile320URL;
	private String mobile728URL;
	private String backupVideoURL;
	private String leaveBehindURL;
	private int secondsBetweenBanners;
	private int perSessionCap;
	private int secondsToExpand;
	private String vidCountdownNumFormat;
	private boolean hasFloatingAd;
	private String adType;
	private String floatHorizontalPosition;
	private String floatVerticalPosition;
	private String expansionAlignHoriz;
	private String expansionAlignVert;
	private int audioVolume;
	private boolean inAdBreakout;
	private boolean outstreamAutoload;
	private String outstreamTriggerId;
	private boolean outstreamFloat;
	private String passbackHTML;
	private String passbackSource; // For html comment only

	public int getAudioVolume()
	{
		return audioVolume;
	}

	public void setAudioVolume(int audioVolume)
	{
		this.audioVolume = audioVolume;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public int getAdSize()
	{
		return adSize;
	}

	public void setAdSize(int adSize)
	{
		this.adSize = adSize;
	}

	public int getVideoWidth()
	{
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth)
	{
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight()
	{
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight)
	{
		this.videoHeight = videoHeight;
	}

	public int getDisplayWidth()
	{
		return displayWidth;
	}

	public void setDisplayWidth(int displayWidth)
	{
		this.displayWidth = displayWidth;
	}

	public int getDisplayHeight()
	{
		return displayHeight;
	}

	public void setDisplayHeight(int displayHeight)
	{
		this.displayHeight = displayHeight;
	}

	public int getBannerAdsPerVideoAd()
	{
		return bannerAdsPerVideoAd;
	}

	public void setBannerAdsPerVideoAd(int bannerAdsPerVideoAd)
	{
		this.bannerAdsPerVideoAd = bannerAdsPerVideoAd;
	}

	public boolean isFrequencyCap()
	{
		return frequencyCap;
	}

	public void setFrequencyCap(boolean frequencyCap)
	{
		this.frequencyCap = frequencyCap;
	}

	public int getFrequencyNumber()
	{
		return frequencyNumber;
	}

	public void setFrequencyNumber(int frequencyNumber)
	{
		this.frequencyNumber = frequencyNumber;
	}

	public String getCombinedDisplayURL()
	{
		return combinedDisplayURL;
	}

	public void setCombinedDisplayURL(String combinedDisplayURL)
	{
		this.combinedDisplayURL = combinedDisplayURL;
	}

	public String getCombinedDisplayLBURL()
	{
		return combinedDisplayLBURL;
	}

	public void setCombinedDisplayLBURL(String combinedDisplayLBURL)
	{
		this.combinedDisplayLBURL = combinedDisplayLBURL;
	}

	public String getMobile320URL()
	{
		return mobile320URL;
	}

	public void setMobile320URL(String mobile320URL)
	{
		this.mobile320URL = mobile320URL;
	}

	public String getMobile728URL()
	{
		return mobile728URL;
	}

	public void setMobile728URL(String mobile728URL)
	{
		this.mobile728URL = mobile728URL;
	}

	public int getSecondsBetweenBanners()
	{
		return secondsBetweenBanners;
	}

	public void setSecondsBetweenBanners(int secondsBetweenBanners)
	{
		this.secondsBetweenBanners = secondsBetweenBanners;
	}

	public int getPerSessionCap()
	{
		return perSessionCap;
	}

	public void setPerSessionCap(int perSessionCap)
	{
		this.perSessionCap = perSessionCap;
	}

	public int getSecondsToExpand()
	{
		return secondsToExpand;
	}

	public void setSecondsToExpand(int secondsToExpand)
	{
		this.secondsToExpand = secondsToExpand;
	}

	public String getVidCountdownNumFormat()
	{
		return vidCountdownNumFormat;
	}

	public void setVidCountdownNumFormat(String vidCountdownNumFormat)
	{
		this.vidCountdownNumFormat = vidCountdownNumFormat;
	}

	public String getBackupVideoURL()
	{
		return backupVideoURL;
	}

	public void setBackupVideoURL(String backupVideoURL)
	{
		this.backupVideoURL = backupVideoURL;
	}

	public String getBackupDisplayURL()
	{
		return backupDisplayURL;
	}

	public void setBackupDisplayURL(String backupDisplayURL)
	{
		this.backupDisplayURL = backupDisplayURL;
	}

	public boolean isHasFloatingAd()
	{
		return hasFloatingAd;
	}

	public void setHasFloatingAd(boolean hasFloatingAd)
	{
		this.hasFloatingAd = hasFloatingAd;
	}

	public String getFloatHorizontalPosition()
	{
		return floatHorizontalPosition;
	}

	public void setFloatHorizontalPosition(String floatHorizontalPosition)
	{
		this.floatHorizontalPosition = floatHorizontalPosition;
	}

	public String getFloatVerticalPosition()
	{
		return floatVerticalPosition;
	}

	public void setFloatVerticalPosition(String floatVerticalPosition)
	{
		this.floatVerticalPosition = floatVerticalPosition;
	}

	public String getLeaveBehindURL()
	{
		return leaveBehindURL;
	}

	public void setLeaveBehindURL(String leaveBehindURL)
	{
		this.leaveBehindURL = leaveBehindURL;
	}

	public String getExpansionAlignHoriz()
	{
		return expansionAlignHoriz;
	}

	public void setExpansionAlignHoriz(String expansionAlignHoriz)
	{
		this.expansionAlignHoriz = expansionAlignHoriz;
	}

	public String getExpansionAlignVert()
	{
		return expansionAlignVert;
	}

	public void setExpansionAlignVert(String expansionAlignVert)
	{
		this.expansionAlignVert = expansionAlignVert;
	}

	public boolean isInAdBreakout() {
		return inAdBreakout;
	}

	public void setInAdBreakout(boolean inAdBreakout) {
		this.inAdBreakout = inAdBreakout;
	}

	public boolean isOutstreamAutoload() {
		return outstreamAutoload;
	}

	public void setOutstreamAutoload(boolean outstreamAutoload) {
		this.outstreamAutoload = outstreamAutoload;
	}

	public String getOutstreamTriggerId() {
		return outstreamTriggerId;
	}

	public void setOutstreamTriggerId(String outstreamTriggerId) {
		this.outstreamTriggerId = outstreamTriggerId;
	}

	public boolean isOutstreamFloat() {
		return outstreamFloat;
	}

	public void setOutstreamFloat(boolean outstreamFloat) {
		this.outstreamFloat = outstreamFloat;
	}

	public String getAdType() {
		return adType;
	}

	public void setAdType(String adType) {
		this.adType = adType;
	}

	public String getPassbackHTML()
	{
		return passbackHTML;
	}

	public void setPassbackHTML(String passbackHTML)
	{
		this.passbackHTML = passbackHTML;
	}

	public String getPassbackSource()
	{
		return passbackSource;
	}

	public void setPassbackSource(String passbackSource)
	{
		this.passbackSource = passbackSource;
	}
}
