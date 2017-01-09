package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by jhaygood on 2/20/16.
 */

@WebSafeModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainManagementModel {

    @JsonProperty("id")
    private int id;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("is_mapped")
    private boolean isMapped;

    @JsonProperty("site_id")
    private Integer siteId;

    @JsonProperty("platform_site_id")
    private Integer platformSiteId;

    @JsonProperty("site_name")
    private String siteName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isMapped() {
        return isMapped;
    }

    public void setIsMapped(boolean isMapped) {
        this.isMapped = isMapped;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getPlatformSiteId() {
        return platformSiteId;
    }

    public void setPlatformSiteId(Integer platformSiteId) {
        this.platformSiteId = platformSiteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}