package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.*;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
public class Site
{

	private static final Logger log = Logger.getLogger(Site.class);

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private AccountPublisher publisher;

	@OneToMany(mappedBy = "site", fetch = FetchType.LAZY)
	private List<Product> products;

	@Column(name = "site_name")
	private String siteName;

	@Column(name = "site_url")
	private String siteUrl;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "site_language")
	@Enumerated(EnumType.STRING)
	private Language language;

	@Column(name = "site_type")
	@Enumerated(EnumType.STRING)
	private SiteType siteType;

	@Column(name = "site_notes")
	private String siteNotes;

	@Column(name = "internal_notes")
	private String internalNotes;

	@Column(name = "site_country")
	@Enumerated(EnumType.STRING)
	private Country siteCountry;

	@Column(name = "traffic_estimate")
	@Enumerated(EnumType.STRING)
	private TrafficEstimate trafficEstimate;

	@Column(name = "adserver_type")
	@Enumerated(EnumType.STRING)
	private AdServerType adServerType;

	@Column(name = "external_id")
	private String externalId;

	@Column(name = "external_id2")
	private String externalId2;

	@Column(name = "liverail_id")
	private String liverailId;

	@Column(name = "is_live")
	private boolean isLive;

	@Column(name = "inherit_pub_details")
	private Boolean inheritPubDetails;

	@Column(name = "is_archived")
	private boolean isArchived;

	@ManyToOne
	@JoinColumn(name = "marketplace_site_id")
	private Site marketplaceSite;

	@ManyToOne
	@JoinColumn(name = "replaced_with_site_id")
	private Site replacedWith;

	@Column(name = "is_default")
	private boolean isDefault;

	@ManyToOne
	@JoinColumn(name = "default_product_id")
	private Product defaultProduct;

	@Column(name = "default_product_format")
	@Enumerated(EnumType.STRING)
	private ProductFormat defaultProductFormat;

	@ManyToOne
	@JoinColumn(name = "marketplace_line_item_id")
	private LineItem marketplaceLineItem;

	@ManyToOne
	@JoinColumn(name = "marketplace_creative_id")
	private Creative marketplaceCreative;

	@Column(name = "passback_tag_contents")
	private String passbackDisplayTagHtml;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getSiteUrl()
	{
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl)
	{
		this.siteUrl = siteUrl;
	}

	public String getContactName()
	{
		return contactName;
	}

	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}

	public String getContactEmail()
	{
		return contactEmail;
	}

	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage(Language language)
	{
		this.language = language;
	}

	public SiteType getSiteType()
	{
		return siteType;
	}

	public void setSiteType(SiteType siteType)
	{
		this.siteType = siteType;
	}

	public List<Product> getProducts()
	{
		return products;
	}

	public void setProducts(List<Product> products)
	{
		this.products = products;
	}

	public void setProductsIterable(Iterable<Product> products)
	{
		this.products = new ArrayList<Product>();
		for (Product item : products) {
			this.products.add(item);
		}
	}

	public String getSiteNotes()
	{
		return siteNotes;
	}

	public void setSiteNotes(String siteNotes)
	{
		this.siteNotes = siteNotes;
	}

	public String getInternalNotes()
	{
		return internalNotes;
	}

	public void setInternalNotes(String internalNotes)
	{
		this.internalNotes = internalNotes;
	}

	public Country getSiteCountry()
	{
		return siteCountry;
	}

	public void setSiteCountry(Country siteCountry)
	{
		this.siteCountry = siteCountry;
	}

	public TrafficEstimate getTrafficEstimate()
	{
		return trafficEstimate;
	}

	public void setTrafficEstimate(TrafficEstimate trafficEstimate)
	{
		this.trafficEstimate = trafficEstimate;
	}

	public AdServerType getAdServerType()
	{
		return adServerType;
	}

	public void setAdServerType(AdServerType adServerType)
	{
		this.adServerType = adServerType;
	}

	public String getExternalId()
	{
		return externalId;
	}

	public void setExternalId(String externalId)
	{
		this.externalId = externalId;
	}

	public String getExternalId2()
	{
		return externalId2;
	}

	public void setExternalId2(String externalId2)
	{
		this.externalId2 = externalId2;
	}

	public boolean isLive()
	{
		return isLive;
	}

	public void setIsLive(boolean isLive)
	{
		this.isLive = isLive;
	}

	public Boolean isInheritPubDetails()
	{
		return inheritPubDetails;
	}

	public void setInheritPubDetails(Boolean inheritPubDetails)
	{
		this.inheritPubDetails = inheritPubDetails;
	}

	public String getLiverailId()
	{
		return liverailId;
	}

	public void setLiverailId(String liverailId)
	{
		this.liverailId = liverailId;
	}

	public boolean isArchived()
	{
		return isArchived;
	}

	public void setArchived(boolean archived)
	{
		isArchived = archived;
	}

	public Site getReplacedWith() {
		return replacedWith;
	}

	public void setReplacedWith(Site replacedWith) {
		this.replacedWith = replacedWith;
	}

	public void setLive(boolean live)
	{
		isLive = live;
	}

	public Boolean getInheritPubDetails()
	{
		return inheritPubDetails;
	}

    public Site getMarketplaceSite() {
        return marketplaceSite;
    }

    public void setMarketplaceSite(Site marketplaceSite) {
        this.marketplaceSite = marketplaceSite;
    }

	public boolean isDefault()
	{
		return isDefault;
	}

	public void setDefault(boolean aDefault)
	{
		isDefault = aDefault;
	}

	public ProductFormat getDefaultProductFormat()
	{
		return defaultProductFormat;
	}

	public void setDefaultProductFormat(ProductFormat defaultProductFormat)
	{
		this.defaultProductFormat = defaultProductFormat;
	}

	public LineItem getMarketplaceLineItem() {
		return marketplaceLineItem;
	}

	public void setMarketplaceLineItem(LineItem marketplaceLineItem) {
		this.marketplaceLineItem = marketplaceLineItem;
	}

	public Creative getMarketplaceCreative() {
		return marketplaceCreative;
	}

	public void setMarketplaceCreative(Creative marketplaceCreative) {
		this.marketplaceCreative = marketplaceCreative;
	}

	public Product getDefaultProduct() {
		return defaultProduct;
	}

	public void setDefaultProduct(Product defaultProduct) {
		this.defaultProduct = defaultProduct;
	}

	public AccountPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(AccountPublisher publisher) {
		this.publisher = publisher;
	}

	public String getPassbackDisplayTagHtml()
	{
		return passbackDisplayTagHtml;
	}

	public void setPassbackDisplayTagHtml(String passbackDisplayTagHtml)
	{
		this.passbackDisplayTagHtml = passbackDisplayTagHtml;
	}
}
