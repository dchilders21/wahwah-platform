package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 12/18/15.
 */

@Entity
@Table(name = "demand_sources")
public class DemandSource {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "name")
    private String name;

    @Column(name = "openx_advertiser_id")
    private Integer oxAdvertiserId;

    @Column(name = "openx_advertiser_uid")
    private String oxAdvertiserUid;

    @Column(name = "ox_default_order_id")
    private Integer oxDefaultOrderId;

    @Column(name = "ox_default_order_uid")
    private String oxDefaultOrderUid;

    @Column(name = "needs_creative_sync")
    private boolean needsCreativeSync;

    @Column(name = "is_direct_advertiser")
    private boolean isDirectAdvertiser;

    @Column(name = "summary_impressions", insertable = false, updatable = false)
    private Long summaryImpressions;

    @Column(name = "summary_fillrate", insertable = false, updatable = false)
    private Integer summaryFillRate;

    @Column(name = "summary_ecpm", insertable = false, updatable = false)
    private Integer summaryECPM;

    @Column(name = "summary_rcpm", insertable = false, updatable = false)
    private Integer summaryRCPM;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOxAdvertiserId() {
        return oxAdvertiserId;
    }

    public void setOxAdvertiserId(Integer oxAdvertiserId) {
        this.oxAdvertiserId = oxAdvertiserId;
    }

    public String getOxAdvertiserUid() {
        return oxAdvertiserUid;
    }

    public void setOxAdvertiserUid(String oxAdvertiserUid) {
        this.oxAdvertiserUid = oxAdvertiserUid;
    }

    public boolean isNeedsCreativeSync() {
        return needsCreativeSync;
    }

    public void setNeedsCreativeSync(boolean needsCreativeSync) {
        this.needsCreativeSync = needsCreativeSync;
    }

    public Long getSummaryImpressions() {
        return summaryImpressions;
    }

    public Integer getSummaryFillRate() {
        return summaryFillRate;
    }

    public Integer getSummaryECPM() {
        return summaryECPM;
    }

    public Integer getSummaryRCPM() {
        return summaryRCPM;
    }

    public Integer getOxDefaultOrderId() {
        return oxDefaultOrderId;
    }

    public void setOxDefaultOrderId(Integer oxDefaultOrderId) {
        this.oxDefaultOrderId = oxDefaultOrderId;
    }

    public String getOxDefaultOrderUid() {
        return oxDefaultOrderUid;
    }

    public void setOxDefaultOrderUid(String oxDefaultOrderUid) {
        this.oxDefaultOrderUid = oxDefaultOrderUid;
    }

    public boolean isDirectAdvertiser() {
        return isDirectAdvertiser;
    }

    public void setDirectAdvertiser(boolean directAdvertiser) {
        isDirectAdvertiser = directAdvertiser;
    }
}
