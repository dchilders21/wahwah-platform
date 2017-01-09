package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 12/9/2015.
 */

@Entity
@Table(name = "line_items")
public class LineItem
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "demand_source_id")
	private DemandSource demandSource;

	@Column(name = "primary_ad_server")
	@Enumerated(EnumType.STRING)
	private AdServerType primaryAdServerType;

	@Column(name = "secondary_ad_server")
	@Enumerated(EnumType.STRING)
	private AdServerType secondaryAdServerType;

	/* openx: id, not uuid
	   liverail: site id (note: lr site id is also in sites table)
	 */
	@Column(name = "primary_ad_server_id")
	private String primaryAdServerId;  // Could be a url for AdServerType.GENERIC_URL, otherwise, most likely an Openx ID

	@Column(name = "secondary_ad_server_id")
	private String secondaryAdServerId;  // Most likely a liverail id

	@Column(name = "ox_ad_server_uid")
	private String oxAdServerUid;  // OpenX requires uid for some things even though its documentation says it shouldn't

	@Column(name = "name")
	private String name;

	@ManyToOne
    @JoinColumn(name = "publisher_id")
	private AccountPublisher publisher;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

	@OneToMany(mappedBy = "lineItem", fetch = FetchType.LAZY)
	private List<LineItemAd> ads;

	@Column(name = "is_archived")
	private boolean isArchived;

	@Column(name = "supports_mobile")
	private Boolean supportsMobile;

	@OneToMany(mappedBy = "lineItem", fetch = FetchType.LAZY)
	private List<LineItemTargeting> targeting;

	@Column(name = "ad_weighting_mode")
	@Enumerated(EnumType.STRING)
	private LineItemAdWeightingMode adWeightingMode;

	@Column(name = "delivery_mode")
	@Enumerated(EnumType.STRING)
	private LineItemDeliveryMode deliveryMode;

	@Column(name = "cpm_type")
	@Enumerated(EnumType.STRING)
	private LineItemCPMType cpmType;

	@Column(name = "expected_cpm_cents")
	private Integer expectedCPMInCents;

	@Column(name = "targeting_cardinality")
	@Enumerated(EnumType.STRING)
	private LineItemTargetingCardinality targetingCardinality;

	public LineItem()
	{
		isArchived = false;
        ads = new ArrayList<>();
		targetingCardinality = LineItemTargetingCardinality.INCLUSIVE;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public AdServerType getPrimaryAdServerType()
	{
		return primaryAdServerType;
	}

	public void setPrimaryAdServerType(AdServerType primaryAdServerType)
	{
		this.primaryAdServerType = primaryAdServerType;
	}

	public String getPrimaryAdServerId()
	{
		return primaryAdServerId;
	}

	public void setPrimaryAdServerId(String primaryAdServerId)
	{
		this.primaryAdServerId = primaryAdServerId;
	}

	public String getSecondaryAdServerId()
	{
		return secondaryAdServerId;
	}

	public void setSecondaryAdServerId(String secondaryAdServerId)
	{
		this.secondaryAdServerId = secondaryAdServerId;
	}

	public String getOxAdServerUid()
	{
		return oxAdServerUid;
	}

	public void setOxAdServerUid(String oxAdServerUid)
	{
		this.oxAdServerUid = oxAdServerUid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<LineItemAd> getAds()
	{
		return ads;
	}

	public void setAds(List<LineItemAd> ads)
	{
		this.ads = ads;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}

    public DemandSource getDemandSource() {
        return demandSource;
    }

    public void setDemandSource(DemandSource demandSource) {
        this.demandSource = demandSource;
    }

    public AccountPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(AccountPublisher publisher) {
        this.publisher = publisher;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

	public AdServerType getSecondaryAdServerType() {
		return secondaryAdServerType;
	}

	public void setSecondaryAdServerType(AdServerType secondaryAdServerType) {
		this.secondaryAdServerType = secondaryAdServerType;
	}

	public Boolean getSupportsMobile()
	{
		return supportsMobile;
	}

	public void setSupportsMobile(Boolean supportsMobile)
	{
		this.supportsMobile = supportsMobile;
	}

	public List<LineItemTargeting> getTargeting()
	{
		return targeting;
	}

	public void setTargeting(List<LineItemTargeting> targeting)
	{
		this.targeting = targeting;
	}

    public LineItemAdWeightingMode getAdWeightingMode() {
        return adWeightingMode;
    }

    public void setAdWeightingMode(LineItemAdWeightingMode adWeightingMode) {
        this.adWeightingMode = adWeightingMode;
    }

    public LineItemDeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(LineItemDeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public Integer getExpectedCPMInCents() {
        return expectedCPMInCents;
    }

    public void setExpectedCPMInCents(Integer expectedCPMInCents) {
        this.expectedCPMInCents = expectedCPMInCents;
    }

	public LineItemCPMType getCpmType() {
		return cpmType;
	}

	public void setCpmType(LineItemCPMType cpmType) {
		this.cpmType = cpmType;
	}

	public LineItemTargetingCardinality getTargetingCardinality()
	{
		return targetingCardinality;
	}

	public void setTargetingCardinality(LineItemTargetingCardinality targetingCardinality)
	{
		this.targetingCardinality = targetingCardinality;
	}
}
