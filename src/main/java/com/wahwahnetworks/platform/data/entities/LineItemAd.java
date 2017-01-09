package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AdServerType;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Brian.Bober on 12/9/2015.
 */

@Entity
@Table(name = "line_item_ads")
public class LineItemAd
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "line_item_id")
	private LineItem lineItem;

	@OneToMany(mappedBy = "lineItem")
	private List<LineItemTargeting> lineItemTargeting;

	@Column(name = "primary_ad_server")
	@Enumerated(EnumType.STRING)
	private AdServerType primaryAdServerType;

	@Column(name = "secondary_ad_server")
	@Enumerated(EnumType.STRING)
	private AdServerType secondaryAdServerType;

	@ManyToOne
	@JoinColumn(name = "creative_id")
	private Creative creative;

	/* openx: id, not uuid
	   liverail: site id (note: lr site id is also in sites table)
	 */
	@Column(name = "primary_ad_server_id")
	private String primaryAdServerId;  // Could be a url for AdServerType.GENERIC_URL, otherwise, most likely an Openx ID

	@Column(name = "ox_ad_server_uid")
	private String oxAdServerUid;  // OpenX requires uid for some things even though its documentation says it shouldn't

	@Column(name = "secondary_ad_server_id")
	private String secondaryAdServerId;  // Most likely a liverail id

	@Column(name = "name")
	private String name;

	@Column(name = "is_archived")
	private boolean isArchived;

	@Column(name = "weight_percentage")
	private Integer weightPercentage;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
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

	public LineItem getLineItem()
	{
		return lineItem;
	}

	public void setLineItem(LineItem lineItem)
	{
		this.lineItem = lineItem;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}

    public Creative getCreative() {
        return creative;
    }

    public void setCreative(Creative creative) {
        this.creative = creative;
    }

	public AdServerType getSecondaryAdServerType() {
		return secondaryAdServerType;
	}

	public void setSecondaryAdServerType(AdServerType secondaryAdServerType) {
		this.secondaryAdServerType = secondaryAdServerType;
	}

    public Integer getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(Integer weightPercentage) {
        this.weightPercentage = weightPercentage;
    }
}
