package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

import java.util.List;

/**
 * Created by jhaygood on 2/19/16.
 */

@WebSafeModel
public class TagSettingsModel {

    @JsonProperty("site")
    private SiteModel site;

    @JsonProperty("network")
    private AccountWebModel network;

    @JsonProperty("domains")
    private List<DomainManagementModel> domains;

    @JsonProperty("suggested_domains")
    private List<DomainManagementModel> suggestedDomains;

    @JsonProperty("tags")
    private List<TagSettingsProductModel> tags;

    @JsonProperty("parent_default_product_format") // Only needed for publisher b/c only used by publisher and site settings "Inherit" option to display resultant type
    private ProductFormat parentDefaultProductFormat;

    @JsonProperty("available_features")
    private List<ClientFeatureWebModel> availableFeatures;

    public SiteModel getSite() {
        return site;
    }

    public void setSite(SiteModel site) {
        this.site = site;
    }

    public List<TagSettingsProductModel> getTags() {
        return tags;
    }

    public void setTags(List<TagSettingsProductModel> tags) {
        this.tags = tags;
    }

    public AccountWebModel getNetwork() {
        return network;
    }

    public void setNetwork(AccountWebModel network) {
        this.network = network;
    }

    public List<DomainManagementModel> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainManagementModel> domains) {
        this.domains = domains;
    }

    public List<DomainManagementModel> getSuggestedDomains() {
        return suggestedDomains;
    }

    public void setSuggestedDomains(List<DomainManagementModel> suggestedDomains) {
        this.suggestedDomains = suggestedDomains;
    }

    public List<ClientFeatureWebModel> getAvailableFeatures() {
        return availableFeatures;
    }

    public void setAvailableFeatures(List<ClientFeatureWebModel> availableFeatures) {
        this.availableFeatures = availableFeatures;
    }

    public ProductFormat getParentDefaultProductFormat()
    {
        return parentDefaultProductFormat;
    }

    public void setParentDefaultProductFormat(ProductFormat parentDefaultProductFormat)
    {
        this.parentDefaultProductFormat = parentDefaultProductFormat;
    }
}
