package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jhaygood on 1/26/16.
 */

@Entity
@DiscriminatorValue("PUBLISHER")
@Table(name="account_publishers")
@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")
public class AccountPublisher extends Account {

    @Column(name = "external_id") // For openx - id
    private String externalId;

    @Column(name = "external_id2") // For openx - uid
    private String externalId2;

    @Column(name = "adserver_type")
    @Enumerated(EnumType.STRING)
    private AdServerType adServerType;

    // Only for some publishers
    @Column(name = "liverail_sitelist_id")
    private String liverailSiteListId;

    // Only for some publishers; won't have redPandaPublisherCreatorId
    @ManyToOne
    @JoinColumn(name = "marketplace_publisher_account_id")
    private AccountPublisher marketplacePublisher;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    private List<Site> sites;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "default_site_id")
    private Integer defaultSiteId;

    @Column(name = "default_product_format")
    @Enumerated(EnumType.STRING)
    private ProductFormat defaultProductFormat;

    @Column(name = "passback_tag_contents")
    private String passbackDisplayTagHtml;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId2() {
        return externalId2;
    }

    public void setExternalId2(String externalId2) {
        this.externalId2 = externalId2;
    }

    public AdServerType getAdServerType() {
        return adServerType;
    }

    public void setAdServerType(AdServerType adServerType) {
        this.adServerType = adServerType;
    }

    public String getLiverailSiteListId() {
        return liverailSiteListId;
    }

    public void setLiverailSiteListId(String liverailSiteListId) {
        this.liverailSiteListId = liverailSiteListId;
    }

    public List<Site> getSites()
    {
        return sites;
    }

    public void setSites(List<Site> sites)
    {
        this.sites = sites;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean aDefault)
    {
        isDefault = aDefault;
    }

    public Integer getDefaultSiteId()
    {
        return defaultSiteId;
    }

    public void setDefaultSiteId(Integer defaultSiteId)
    {
        this.defaultSiteId = defaultSiteId;
    }

    public ProductFormat getDefaultProductFormat()
    {
        return defaultProductFormat;
    }

    public void setDefaultProductFormat(ProductFormat defaultProductFormat)
    {
        this.defaultProductFormat = defaultProductFormat;
    }

    public AccountPublisher getMarketplacePublisher() {
        return marketplacePublisher;
    }

    public void setMarketplacePublisher(AccountPublisher marketplacePublisher) {
        this.marketplacePublisher = marketplacePublisher;
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
