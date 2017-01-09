package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AdServerType;
import com.wahwahnetworks.platform.data.entities.enums.CreativePlatform;
import com.wahwahnetworks.platform.data.entities.enums.CreativeType;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/14/16.
 */

@Entity
@Table(name = "creatives")
public class Creative {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "demand_source_id")
    private DemandSource demandSource;

    @Column(name = "creative_type")
    @Enumerated(EnumType.STRING)
    private CreativeType creativeType;

    @Column(name = "vendor_type")
    private String vendorType;

    @Column(name = "tag_contents")
    private String tagContents;

    @Column(name = "primary_ad_server")
    @Enumerated(EnumType.STRING)
    private AdServerType primaryAdServerType;

    @Column(name = "secondary_ad_server")
    @Enumerated(EnumType.STRING)
    private AdServerType secondaryAdServerType;

    @Column(name = "primary_ad_server_id")
    private String primaryAdServerId;  // Could be a url for AdServerType.GENERIC_URL, otherwise, most likely an Openx ID

    @Column(name = "secondary_ad_server_id")
    private String secondaryAdServerId;  // Most likely a liverail id

    @Column(name = "creative_platform")
    @Enumerated(EnumType.STRING)
    private CreativePlatform creativePlatform;

    @Column(name = "is_biddable")
    private boolean isBiddable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DemandSource getDemandSource() {
        return demandSource;
    }

    public void setDemandSource(DemandSource demandSource) {
        this.demandSource = demandSource;
    }

    public CreativeType getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(CreativeType creativeType) {
        this.creativeType = creativeType;
    }

    public String getTagContents() {
        return tagContents;
    }

    public void setTagContents(String tagContents) {
        this.tagContents = tagContents;
    }

    public AdServerType getPrimaryAdServerType() {
        return primaryAdServerType;
    }

    public void setPrimaryAdServerType(AdServerType primaryAdServerType) {
        this.primaryAdServerType = primaryAdServerType;
    }

    public String getPrimaryAdServerId() {
        return primaryAdServerId;
    }

    public void setPrimaryAdServerId(String primaryAdServerId) {
        this.primaryAdServerId = primaryAdServerId;
    }

    public String getSecondaryAdServerId() {
        return secondaryAdServerId;
    }

    public void setSecondaryAdServerId(String secondaryAdServerId) {
        this.secondaryAdServerId = secondaryAdServerId;
    }

    public AdServerType getSecondaryAdServerType() {
        return secondaryAdServerType;
    }

    public void setSecondaryAdServerType(AdServerType secondaryAdServerType) {
        this.secondaryAdServerType = secondaryAdServerType;
    }

    public boolean isBiddable() {
        return isBiddable;
    }

    public void setBiddable(boolean biddable) {
        isBiddable = biddable;
    }

    public String getVendorType() {
        return vendorType;
    }

    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

    public CreativePlatform getCreativePlatform() {
        return creativePlatform;
    }

    public void setCreativePlatform(CreativePlatform creativePlatform) {
        this.creativePlatform = creativePlatform;
    }
}
