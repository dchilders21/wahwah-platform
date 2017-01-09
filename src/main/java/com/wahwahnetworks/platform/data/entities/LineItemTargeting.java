package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleDisplayType;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Brian.Bober on 4/18/2016.
 */


@Entity
@Table(name = "line_item_targeting")
public class LineItemTargeting
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name= "type")
	private LineItemTargetingRuleType type;

    @Column(name= "display_type")
	@Enumerated(EnumType.STRING)
    private LineItemTargetingRuleDisplayType displayType;

	@NotNull
	@Column(name= "name")
	private String name;

	@NotNull
    @ManyToOne
	@JoinColumn(name = "line_item_id")
	private LineItem lineItem;

	/* Only one of the following will be set */
    @ManyToOne
	@JoinColumn(name= "publisher_id")
	private AccountPublisher publisher;

    @ManyToOne
	@JoinColumn(name= "site_id")
	private Site site;

    @ManyToOne
	@JoinColumn(name= "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "network_id")
	private AccountNetwork network;

	public LineItemTargetingRuleType getType()
	{
		return type;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setType(LineItemTargetingRuleType type)
	{
		this.type = type;
	}

	public LineItemTargetingRuleDisplayType getDisplayType()
	{
		return displayType;
	}

	public void setDisplayType(LineItemTargetingRuleDisplayType displayType)
	{
		this.displayType = displayType;
	}

	public LineItem getLineItem() {
		return lineItem;
	}

	public void setLineItem(LineItem lineItem) {
		this.lineItem = lineItem;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

    public AccountNetwork getNetwork() {
        return network;
    }

    public void setNetwork(AccountNetwork network) {
        this.network = network;
    }
}