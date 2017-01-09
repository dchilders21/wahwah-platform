package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

import javax.persistence.*;

/**
 * Created by jhaygood on 1/26/16.
 */

@Entity
@DiscriminatorValue("NETWORK")
@Table(name="account_networks")
@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")
public class AccountNetwork extends Account
{
    @ManyToOne
    @JoinColumn(name = "marketplace_demand_source_id")
    private DemandSource marketplaceDemandSource;

    @Column(name = "ox_network_id")
    private String openxNetworkId;

    @Column(name = "ox_network_uid")
    private String openxNetworkUid;

    @Column(name = "lr_ox_advertiser_id")
    private Integer liverailOpenXAdvertiserId;

    @Column(name = "lr_ox_advertiser_uid")
    private String liverailOpenXAdvertiserUid;

    @Column(name = "lr_ox_order_id")
    private Integer liverailOpenXOrderId;

    @Column(name = "lr_ox_order_uid")
    private String liverailOpenXOrderUid;

    @Column(name = "ox_marketplace_advertiser_id")
    private Integer marketplaceOpenXAdvertiserId;

    @Column(name = "ox_marketplace_advertiser_uid")
    private String marketplaceOpenXAdvertiserUid;

    @Column(name = "ox_marketplace_order_id")
    private Integer marketplaceOpenXOrderId;

    @Column(name = "ox_marketplace_order_uid")
    private String marketplaceOpenXOrderUid;

    @Column(name = "ox_liverail_wwn_order_id") // Under advertiser ccount 537098205
    private Integer liverailWWNOpenXOrderId;

    @Column(name = "ox_liverail_wwn_order_uid") // Under advertiser account 537098205
    private String liverailWWNOpenXOrderUid;

    @ManyToOne
    @JoinColumn(name = "single_publisher_id")
    private AccountPublisher singlePublisher;

    @Column(name = "default_publisher_id")
    private Integer defaultPublisherId;

    @Column(name = "default_product_format")
    @Enumerated(EnumType.STRING)
    private ProductFormat defaultProductFormat;

    @Column(name = "passback_tag_contents")
    private String passbackDisplayTagHtml;

    public String getOpenxNetworkId()
    {
        return openxNetworkId;
    }

    public void setOpenxNetworkId(String openxNetworkId)
    {
        this.openxNetworkId = openxNetworkId;
    }

    public String getOpenxNetworkUid()
    {
        return openxNetworkUid;
    }

    public void setOpenxNetworkUid(String openxNetworkUid)
    {
        this.openxNetworkUid = openxNetworkUid;
    }

    public Integer getLiverailOpenXAdvertiserId()
    {
        return liverailOpenXAdvertiserId;
    }

    public void setLiverailOpenXAdvertiserId(Integer liverailOpenXAdvertiserId)
    {
        this.liverailOpenXAdvertiserId = liverailOpenXAdvertiserId;
    }

    public Integer getLiverailOpenXOrderId()
    {
        return liverailOpenXOrderId;
    }

    public void setLiverailOpenXOrderId(Integer liverailOpenXOrderId)
    {
        this.liverailOpenXOrderId = liverailOpenXOrderId;
    }

    public Integer getMarketplaceOpenXAdvertiserId()
    {
        return marketplaceOpenXAdvertiserId;
    }

    public void setMarketplaceOpenXAdvertiserId(Integer marketplaceOpenXAdvertiserId)
    {
        this.marketplaceOpenXAdvertiserId = marketplaceOpenXAdvertiserId;
    }

    public String getLiverailOpenXAdvertiserUid()
    {
        return liverailOpenXAdvertiserUid;
    }

    public void setLiverailOpenXAdvertiserUid(String liverailOpenXAdvertiserUid)
    {
        this.liverailOpenXAdvertiserUid = liverailOpenXAdvertiserUid;
    }

    public String getLiverailOpenXOrderUid()
    {
        return liverailOpenXOrderUid;
    }

    public void setLiverailOpenXOrderUid(String liverailOpenXOrderUid)
    {
        this.liverailOpenXOrderUid = liverailOpenXOrderUid;
    }

    public String getMarketplaceOpenXAdvertiserUid()
    {
        return marketplaceOpenXAdvertiserUid;
    }

    public void setMarketplaceOpenXAdvertiserUid(String marketplaceOpenXAdvertiserUid)
    {
        this.marketplaceOpenXAdvertiserUid = marketplaceOpenXAdvertiserUid;
    }

    public Integer getMarketplaceOpenXOrderId()
    {
        return marketplaceOpenXOrderId;
    }

    public void setMarketplaceOpenXOrderId(Integer marketplaceOpenXOrderId)
    {
        this.marketplaceOpenXOrderId = marketplaceOpenXOrderId;
    }

    public String getMarketplaceOpenXOrderUid()
    {
        return marketplaceOpenXOrderUid;
    }

    public void setMarketplaceOpenXOrderUid(String marketplaceOpenXOrderUid)
    {
        this.marketplaceOpenXOrderUid = marketplaceOpenXOrderUid;
    }

    public Integer getLiverailWWNOpenXOrderId()
    {
        return liverailWWNOpenXOrderId;
    }

    public void setLiverailWWNOpenXOrderId(Integer liverailWWNOpenXOrderId)
    {
        this.liverailWWNOpenXOrderId = liverailWWNOpenXOrderId;
    }

    public String getLiverailWWNOpenXOrderUid()
    {
        return liverailWWNOpenXOrderUid;
    }

    public void setLiverailWWNOpenXOrderUid(String liverailWWNOpenXOrderUid)
    {
        this.liverailWWNOpenXOrderUid = liverailWWNOpenXOrderUid;
    }

    public AccountPublisher getSinglePublisher() {
        return singlePublisher;
    }

    public void setSinglePublisher(AccountPublisher singlePublisher) {
        this.singlePublisher = singlePublisher;
    }

    public boolean isSinglePublisher(){
        return singlePublisher != null;
    }

    public Integer getDefaultPublisherId()
    {
        return defaultPublisherId;
    }

    public void setDefaultPublisherId(Integer defaultPublisherId)
    {
        this.defaultPublisherId = defaultPublisherId;
    }

    public ProductFormat getDefaultProductFormat()
    {
        return defaultProductFormat;
    }

    public void setDefaultProductFormat(ProductFormat defaultProductFormat)
    {
        this.defaultProductFormat = defaultProductFormat;
    }

    public DemandSource getMarketplaceDemandSource() {
        return marketplaceDemandSource;
    }

    public void setMarketplaceDemandSource(DemandSource marketplaceDemandSource) {
        this.marketplaceDemandSource = marketplaceDemandSource;
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