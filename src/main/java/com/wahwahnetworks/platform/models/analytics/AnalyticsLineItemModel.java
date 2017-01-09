package com.wahwahnetworks.platform.models.analytics;

/**
 * Created by jhaygood on 4/16/16.
 */
public class AnalyticsLineItemModel {
    private int id;
    private Integer accountId;
    private AnalyticsAdServerType adServerType;
    private String externalId;
    private Integer demandPartnerId;
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public AnalyticsAdServerType getAdServerType() {
        return adServerType;
    }

    public void setAdServerType(AnalyticsAdServerType adServerType) {
        this.adServerType = adServerType;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getDemandPartnerId() {
        return demandPartnerId;
    }

    public void setDemandPartnerId(Integer demandPartnerId) {
        this.demandPartnerId = demandPartnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
