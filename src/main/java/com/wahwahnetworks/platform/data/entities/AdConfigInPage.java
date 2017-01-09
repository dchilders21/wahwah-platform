package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.entities.enums.AdUnitType;
import com.wahwahnetworks.platform.models.web.AdConfigInPageModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "adconfig_inpage")
public class AdConfigInPage implements Serializable
{

	private static final Logger log = Logger.getLogger(AdConfigInPage.class);

	@Id
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "product_id", nullable = false)
	Product product;

	@Column(name = "enabled", nullable = true)
	private Boolean enabled = false;

	@Column(name = "freq_cap_enabled")
	private Boolean freqCapEnabled;

	@Column(name = "freq_cap_num")
	private int freqCapValue;

	@Column(name = "audio_volume")
	private int audioVolume;

	@Column(name = "vid_countdown_num_format")
	private String vidCountdownNumFormat;

	@Column(name = "banner_ads_per_video_ad")
	private int bannerAdsPerVideoAd;

	@Column(name = "seconds_between_banners")
	private int secondsBetweenBanners;

	@Column(name = "per_session_cap")
	private int perSessionCap;

	@Column(name = "seconds_to_expand")
	private int secondsToExpand;

	@ManyToOne
	@JoinColumn(name = "combined_adunit_id")
	private AdUnit primaryDisplayAdUnit;

	@ManyToOne
	@JoinColumn(name = "backup_adunit_id")
	private AdUnit backupDisplayAdUnit;

	@ManyToOne
	@JoinColumn(name = "video_adunit_id")
	private AdUnit videoAdUnit;

	@ManyToOne
	@JoinColumn(name = "mobile_adunit_id")
	private AdUnit mobileAdUnit;

	@ManyToOne
	@JoinColumn(name = "tablet_adunit_id")
	private AdUnit tabletAdUnit;

	@ManyToOne
	@JoinColumn(name = "leavebehind_adunit_id")
	private AdUnit leaveBehindAdUnit;

	public AdConfigInPage()
	{

	}

	public AdConfigInPage(Product product)
	{
		this.product = product;
	}

	public void updateFromModel(AdConfigInPageModel inpageModel)
	{
		/* widget and id not set here, only properties */
		this.setEnabled(inpageModel.getEnabled());
		this.setFreqCapEnabled(inpageModel.getFreqCapEnabled());
		this.setFreqCapValue(inpageModel.getFreqCapValue());
		this.setVidCountdownNumFormat(inpageModel.getVidCountdownNumFormat());
		this.setBannerAdsPerVideoAd(inpageModel.getBannerAdsPerVideoAd());
		this.setSecondsBetweenBanners(inpageModel.getSecondsBetweenBanners());
		this.setPerSessionCap(inpageModel.getPerSessionCap());
		this.setSecondsToExpand(inpageModel.getSecondsToExpand());
		this.setAudioVolume(inpageModel.getAudioVolume());
		// Set up adunits
		int dispWidth = inpageModel.getDisplayWidth();
		int dispHeight = inpageModel.getDisplayHeight();
		int vidWidth = inpageModel.getVideoWidth();
		int vidHeight = inpageModel.getVideoHeight();
		if (this.getPrimaryDisplayAdUnit() == null)
			this.setPrimaryDisplayAdUnit(createAdUnitHelper(inpageModel.getPrimaryDisplayAdServerUnitId(), AdServerType.OPEN_X, dispWidth, dispHeight, AdUnitType.DISPLAY));
		if (this.getVideoAdUnit() == null)
			this.setVideoAdUnit(createAdUnitHelper(inpageModel.getVideoAdServerUnitId(), AdServerType.OPEN_X, vidWidth, vidHeight, AdUnitType.VIDEO));
		if (this.getBackupDisplayAdUnit() == null)
			this.setBackupDisplayAdUnit(createAdUnitHelper(inpageModel.getBackupDisplayAdServerUnitId(), AdServerType.OPEN_X, dispWidth, dispHeight, AdUnitType.DISPLAY));
		if (this.getLeaveBehindAdUnit() == null)
			this.setLeaveBehindAdUnit(createAdUnitHelper(inpageModel.getLeaveBehindAdServerUnitId(), AdServerType.OPEN_X, dispWidth, dispHeight, AdUnitType.DISPLAY));
		if (this.getMobileAdUnit() == null)
			this.setMobileAdUnit(createAdUnitHelper(inpageModel.getMobileAdServerUnitId(), AdServerType.OPEN_X, 320, 50, AdUnitType.MOBILE));
		if (this.getTabletAdUnit() == null)
			this.setTabletAdUnit(createAdUnitHelper(inpageModel.getTabletAdServerUnitId(), AdServerType.OPEN_X, 728, 90, AdUnitType.MOBILE));
	}

	private AdUnit createAdUnitHelper(String adServerUnitId, AdServerType adServerType, int width, int height, AdUnitType adType)
	{
		if (!StringUtils.isEmpty(adServerUnitId))
		{
			AdUnit adUnit = new AdUnit();
			adUnit.setAdServerUnitId(adServerUnitId);
			adUnit.setAdServerType(adServerType);
			adUnit.setWidth(width);
			adUnit.setHeight(height);
			adUnit.setProduct(this.getProduct());
			adUnit.setAdUnitType(adType);
			adUnit.setPlatformCreated(true);
			return adUnit;
		}
		else
			return null;
	}


	public Product getProduct()
	{
		return product;
	}

	public void setProduct(Product product)
	{
		this.product = product;
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
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

	public AdUnit getPrimaryDisplayAdUnit()
	{
		return primaryDisplayAdUnit;
	}

	public void setPrimaryDisplayAdUnit(AdUnit primaryDisplayAdUnit)
	{
		this.primaryDisplayAdUnit = primaryDisplayAdUnit;
	}

	public AdUnit getBackupDisplayAdUnit()
	{
		return backupDisplayAdUnit;
	}

	public void setBackupDisplayAdUnit(AdUnit backupDisplayAdUnit)
	{
		this.backupDisplayAdUnit = backupDisplayAdUnit;
	}

	public AdUnit getVideoAdUnit()
	{
		return videoAdUnit;
	}

	public void setVideoAdUnit(AdUnit videoAdUnit)
	{
		this.videoAdUnit = videoAdUnit;
	}

	public AdUnit getMobileAdUnit()
	{
		return mobileAdUnit;
	}

	public void setMobileAdUnit(AdUnit mobileAdUnit)
	{
		this.mobileAdUnit = mobileAdUnit;
	}

	public AdUnit getTabletAdUnit()
	{
		return tabletAdUnit;
	}

	public void setTabletAdUnit(AdUnit tabletAdUnit)
	{
		this.tabletAdUnit = tabletAdUnit;
	}

	public AdUnit getLeaveBehindAdUnit()
	{
		return leaveBehindAdUnit;
	}

	public void setLeaveBehindAdUnit(AdUnit leaveBehindAdUnit)
	{
		this.leaveBehindAdUnit = leaveBehindAdUnit;
	}

	public Boolean isEnabled()
	{
		return enabled;
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

	// Convenience methods for constructing model


	public int getDisplayWidth()
	{
		if (primaryDisplayAdUnit != null)
			return primaryDisplayAdUnit.getWidth();
		else
			return 300;
	}

	public int getDisplayHeight()
	{
		if (primaryDisplayAdUnit != null)
			return primaryDisplayAdUnit.getHeight();
		else
			return 250;
	}

	public int getVideoWidth()
{
	if (videoAdUnit != null)
		return videoAdUnit.getWidth();
	else
		return 558;
}

	public int getVideoHeight()
	{
		if (videoAdUnit != null)
			return videoAdUnit.getHeight();
		else
			return 314;
	}
}
