package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AdConfigInPage;

import javax.validation.constraints.Size;

@WebSafeModel
public class AdConfigInPageModel
{

	@JsonProperty("enabled")
	private Boolean enabled;

	@JsonProperty("display_width")
	private int displayWidth;

	@JsonProperty("display_height")
	private int displayHeight;

	@JsonProperty("video_width")
	private int videoWidth;

	@JsonProperty("video_height")
	private int videoHeight;

	@JsonProperty("primary_display_adunit_id") /* AdOnly at current time */
	private int primaryDisplayAdUnitId;

	@JsonProperty("primary_display_external_id")
	@Size(max = 65535)
	private String primaryDisplayAdServerUnitId;

	@JsonProperty("primary_display_adserver_type")
	@Size(max = 65535)
	private String primaryDisplayAdServerType;

	@JsonProperty("backup_display_adunit_id") /* AdOnly at current time */
	private int backupDisplayAdUnitId;

	@JsonProperty("backup_display_external_id")
	@Size(max = 65535)
	private String backupDisplayAdServerUnitId;

	@JsonProperty("backup_display_adserver_type")
	@Size(max = 65535)
	private String backupDisplayAdServerType;

	@JsonProperty("video_adunit_id") /* AdOnly at current time */
	private int videoAdUnitId;

	@JsonProperty("video_external_id")
	@Size(max = 65535)
	private String videoAdServerUnitId;

	@JsonProperty("video_adserver_type")
	@Size(max = 65535)
	private String videoAdServerType;

	@JsonProperty("tablet_adunit_id") /* AdOnly at current time */
	private int tabletAdUnitId;

	@JsonProperty("tablet_external_id")
	@Size(max = 65535)
	private String tabletAdServerUnitId;

	@JsonProperty("tablet_adserver_type")
	@Size(max = 65535)
	private String tabletAdServerType;

	@JsonProperty("mobile_adunit_id") /* AdOnly at current time */
	private int mobileAdUnitId;

	@JsonProperty("mobile_external_id")
	@Size(max = 65535)
	private String mobileAdServerUnitId;

	@JsonProperty("mobile_adserver_type")
	@Size(max = 65535)
	private String mobileAdServerType;

	@JsonProperty("leave_behind_adunit_id") /* AdOnly at current time */
	private int leaveBehindAdUnitId;

	@JsonProperty("leave_behind_external_id") /* AdOnly at current time */
	@Size(max = 65535)
	private String leaveBehindAdServerUnitId;

	@JsonProperty("leave_behind_adserver_type")
	@Size(max = 65535)
	private String leaveBehindAdServerType;

	@JsonProperty("vid_countdown_num_format")
	@Size(max = 20)
	private String vidCountdownNumFormat;

	@JsonProperty("banner_ads_per_video_ad")
	private int bannerAdsPerVideoAd;

	@JsonProperty("seconds_between_banners")
	private int secondsBetweenBanners;

	@JsonProperty("per_session_cap")
	private int perSessionCap;

	@JsonProperty("seconds_to_expand")
	private int secondsToExpand;

	@JsonProperty("freq_cap_enabled")
	private Boolean freqCapEnabled;

	@JsonProperty("freq_cap_num")
	private int freqCapValue;

	@JsonProperty("audio_volume")
	private int audioVolume;

	public AdConfigInPageModel()
	{

	}

	public AdConfigInPageModel(AdConfigInPage inpage)
	{
		this.setEnabled(inpage.getEnabled());
		this.setVideoWidth(inpage.getVideoWidth());
		this.setVideoHeight(inpage.getVideoHeight());
		this.setDisplayWidth(inpage.getDisplayWidth());
		this.setDisplayHeight(inpage.getDisplayHeight());
		this.setFreqCapEnabled(inpage.getFreqCapEnabled());
		this.setFreqCapValue(inpage.getFreqCapValue());
		this.setVidCountdownNumFormat(inpage.getVidCountdownNumFormat());
		this.setBannerAdsPerVideoAd(inpage.getBannerAdsPerVideoAd());
		this.setSecondsBetweenBanners(inpage.getSecondsBetweenBanners());
		this.setPerSessionCap(inpage.getPerSessionCap());
		this.setSecondsToExpand(inpage.getSecondsToExpand());
		this.setAudioVolume(inpage.getAudioVolume());
		// Ad units
		if (inpage.getPrimaryDisplayAdUnit() != null)
		{
			this.setPrimaryDisplayAdUnitId(inpage.getPrimaryDisplayAdUnit().getId());
			this.setPrimaryDisplayAdServerUnitId(inpage.getPrimaryDisplayAdUnit().getAdServerUnitId());
			this.setPrimaryDisplayAdServerType(inpage.getPrimaryDisplayAdUnit().getAdServerType().toString());
		}
		if (inpage.getBackupDisplayAdUnit() != null)
		{
			this.setBackupDisplayAdUnitId(inpage.getBackupDisplayAdUnit().getId());
			this.setBackupDisplayAdServerUnitId(inpage.getBackupDisplayAdUnit().getAdServerUnitId());
			this.setBackupDisplayAdServerType(inpage.getBackupDisplayAdUnit().getAdServerType().toString());
		}
		if (inpage.getVideoAdUnit() != null)
		{
			this.setVideoAdUnitId(inpage.getVideoAdUnit().getId());
			this.setVideoAdServerUnitId(inpage.getVideoAdUnit().getAdServerUnitId());
			this.setVideoAdServerType(inpage.getVideoAdUnit().getAdServerType().toString());
		}
		if (inpage.getTabletAdUnit() != null)
		{
			this.setTabletAdUnitId(inpage.getTabletAdUnit().getId());
			this.setTabletAdServerUnitId(inpage.getTabletAdUnit().getAdServerUnitId());
			this.setTabletAdServerType(inpage.getTabletAdUnit().getAdServerType().toString());
		}
		if (inpage.getMobileAdUnit() != null)
		{
			this.setMobileAdUnitId(inpage.getMobileAdUnit().getId());
			this.setMobileAdServerUnitId(inpage.getMobileAdUnit().getAdServerUnitId());
			this.setMobileAdServerType(inpage.getMobileAdUnit().getAdServerType().toString());
		}
		if (inpage.getLeaveBehindAdUnit() != null)
		{
			this.setLeaveBehindAdUnitId(inpage.getLeaveBehindAdUnit().getId());
			this.setLeaveBehindAdServerUnitId(inpage.getLeaveBehindAdUnit().getAdServerUnitId());
			this.setLeaveBehindAdServerType(inpage.getLeaveBehindAdUnit().getAdServerType().toString());
		}
	}

	public String getVidCountdownNumFormat()
	{
		return vidCountdownNumFormat;
	}

	public void setVidCountdownNumFormat(String vidCountdownNumFormat)
	{
		this.vidCountdownNumFormat = vidCountdownNumFormat;
	}

	public int getBannerAdsPerVideoAd()
	{
		return bannerAdsPerVideoAd;
	}

	public void setBannerAdsPerVideoAd(int bannerAdsPerVideoAd)
	{
		this.bannerAdsPerVideoAd = bannerAdsPerVideoAd;
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


	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public int getDisplayWidth()
	{
		return displayWidth;
	}

	public void setDisplayWidth(int width)
	{
		this.displayWidth = width;
	}

	public int getDisplayHeight()
	{
		return displayHeight;
	}

	public void setDisplayHeight(int height)
	{
		this.displayHeight = height;
	}

	public int getVideoWidth()
	{
		return videoWidth;
	}

	public void setVideoWidth(int width)
	{
		this.videoWidth = width;
	}

	public int getVideoHeight()
	{
		return videoHeight;
	}

	public void setVideoHeight(int height)
	{
		this.videoHeight = height;
	}

	public Boolean getFreqCapEnabled()
	{
		return freqCapEnabled;
	}

	public void setFreqCapEnabled(Boolean freqCapEnabled)
	{
		this.freqCapEnabled = freqCapEnabled;
	}


	public int getFreqCapValue()
	{
		return freqCapValue;
	}

	public void setFreqCapValue(int freqCapValue)
	{
		this.freqCapValue = freqCapValue;
	}

	public String getPrimaryDisplayAdServerUnitId()
	{
		return primaryDisplayAdServerUnitId;
	}

	public void setPrimaryDisplayAdServerUnitId(String primaryDisplayAdServerUnitId)
	{
		this.primaryDisplayAdServerUnitId = primaryDisplayAdServerUnitId;
	}

	public String getPrimaryDisplayAdServerType()
	{
		return primaryDisplayAdServerType;
	}

	public void setPrimaryDisplayAdServerType(String primaryDisplayAdServerType)
	{
		this.primaryDisplayAdServerType = primaryDisplayAdServerType;
	}

	public String getBackupDisplayAdServerUnitId()
	{
		return backupDisplayAdServerUnitId;
	}

	public void setBackupDisplayAdServerUnitId(String backupDisplayAdServerUnitId)
	{
		this.backupDisplayAdServerUnitId = backupDisplayAdServerUnitId;
	}

	public String getBackupDisplayAdServerType()
	{
		return backupDisplayAdServerType;
	}

	public void setBackupDisplayAdServerType(String backupDisplayAdServerType)
	{
		this.backupDisplayAdServerType = backupDisplayAdServerType;
	}

	public String getVideoAdServerUnitId()
	{
		return videoAdServerUnitId;
	}

	public void setVideoAdServerUnitId(String videoAdServerUnitId)
	{
		this.videoAdServerUnitId = videoAdServerUnitId;
	}

	public String getVideoAdServerType()
	{
		return videoAdServerType;
	}

	public void setVideoAdServerType(String videoAdServerType)
	{
		this.videoAdServerType = videoAdServerType;
	}

	public String getTabletAdServerUnitId()
	{
		return tabletAdServerUnitId;
	}

	public void setTabletAdServerUnitId(String tabletAdServerUnitId)
	{
		this.tabletAdServerUnitId = tabletAdServerUnitId;
	}

	public String getTabletAdServerType()
	{
		return tabletAdServerType;
	}

	public void setTabletAdServerType(String tabletAdServerType)
	{
		this.tabletAdServerType = tabletAdServerType;
	}

	public String getMobileAdServerUnitId()
	{
		return mobileAdServerUnitId;
	}

	public void setMobileAdServerUnitId(String mobileAdServerUnitId)
	{
		this.mobileAdServerUnitId = mobileAdServerUnitId;
	}

	public String getMobileAdServerType()
	{
		return mobileAdServerType;
	}

	public void setMobileAdServerType(String mobileAdServerType)
	{
		this.mobileAdServerType = mobileAdServerType;
	}

	public String getLeaveBehindAdServerUnitId()
	{
		return leaveBehindAdServerUnitId;
	}

	public void setLeaveBehindAdServerUnitId(String leaveBehindAdServerUnitId)
	{
		this.leaveBehindAdServerUnitId = leaveBehindAdServerUnitId;
	}

	public String getLeaveBehindAdServerType()
	{
		return leaveBehindAdServerType;
	}

	public void setLeaveBehindAdServerType(String leaveBehindAdserverType)
	{
		this.leaveBehindAdServerType = leaveBehindAdserverType;
	}

	public Boolean isEnabled()
	{
		return enabled;
	}

	public int getPrimaryDisplayAdUnitId()
	{
		return primaryDisplayAdUnitId;
	}

	public void setPrimaryDisplayAdUnitId(int primaryDisplayAdunitId)
	{
		this.primaryDisplayAdUnitId = primaryDisplayAdunitId;
	}

	public int getBackupDisplayAdUnitId()
	{
		return backupDisplayAdUnitId;
	}

	public void setBackupDisplayAdUnitId(int backupDisplayAdunitId)
	{
		this.backupDisplayAdUnitId = backupDisplayAdunitId;
	}

	public int getVideoAdUnitId()
	{
		return videoAdUnitId;
	}

	public void setVideoAdUnitId(int videoAdUnitId)
	{
		this.videoAdUnitId = videoAdUnitId;
	}

	public int getTabletAdUnitId()
	{
		return tabletAdUnitId;
	}

	public void setTabletAdUnitId(int tabletAdUnitId)
	{
		this.tabletAdUnitId = tabletAdUnitId;
	}

	public int getMobileAdUnitId()
	{
		return mobileAdUnitId;
	}

	public void setMobileAdUnitId(int mobileAdUnitId)
	{
		this.mobileAdUnitId = mobileAdUnitId;
	}

	public int getLeaveBehindAdUnitId()
	{
		return leaveBehindAdUnitId;
	}

	public void setLeaveBehindAdUnitId(int leaveBehindAdUnitId)
	{
		this.leaveBehindAdUnitId = leaveBehindAdUnitId;
	}

	public Boolean isFreqCapEnabled()
	{
		return freqCapEnabled;
	}

	public int getAudioVolume()
	{
		return audioVolume;
	}

	public void setAudioVolume(int audioVolume)
	{
		if (audioVolume > 100)
			audioVolume = 100;
		if (audioVolume < 0)
			audioVolume = 0;
		this.audioVolume = audioVolume;
	}

	@Override
	public String toString()
	{
		return "AdConfigInPageModel [enabled=" + enabled + ", displayWidth="
				+ displayWidth + ", displayHeight=" + displayHeight + ", videoWidth" + videoWidth + ", videoHeight=" + videoHeight + ","
				+ "primaryDisplay={ id=" + primaryDisplayAdServerUnitId + ", type=" + primaryDisplayAdServerType + "},"
				+ "backupDisplay={ id=" + backupDisplayAdServerUnitId + ", type=" + backupDisplayAdServerType + "},"
				+ "video={ id=" + videoAdServerUnitId + ", type=" + videoAdServerType + "},"
				+ "tablet={ id=" + tabletAdServerUnitId + ", type=" + tabletAdServerType + "},"
				+ "mobile={ id=" + mobileAdServerUnitId + ", type=" + mobileAdServerType + "},"
				+ "leavebehind={ id=" + leaveBehindAdServerUnitId + ", type=" + primaryDisplayAdServerType + "},"
				+ "primaryDisplay={ id=" + primaryDisplayAdServerUnitId + ", type=" + leaveBehindAdServerType + "},"
				+ "freqCapEnabled=" + freqCapEnabled + " , freqCapValue=" + freqCapValue + ", vidCountdownNumFormat="
				+ vidCountdownNumFormat + ", bannerAdsPerVideoAd=" + bannerAdsPerVideoAd + ", secondsBetweenBanners="
				+ secondsBetweenBanners + ", perSessionCap=" + perSessionCap + ", secondsToExpand=" + secondsToExpand
				+ "]";
	}

}
