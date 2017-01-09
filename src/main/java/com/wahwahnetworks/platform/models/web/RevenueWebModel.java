package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.RevenueModel;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;
import com.wahwahnetworks.platform.data.entities.enums.PublisherRevenueModelType;

import java.time.LocalDate;

/**
 * Created by jhaygood on 4/8/16.
 */

@WebSafeModel
public class RevenueWebModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("revenue_model_start_date")
    private LocalDate effectiveStartDate;

    @JsonProperty("revenue_model_end_date")
    private LocalDate effectiveEndDate;

    @JsonProperty("revenue_model_type")
    private PublisherRevenueModelType revenueModelType;

    @JsonProperty("format")
    private ProductFormat format;

    @JsonProperty("revenue_share_percent")
    private String revenueSharePercentage;

    @JsonProperty("us_desktop_gcpm")
    private String guaranteedCPMForUSDesktop;

    @JsonProperty("gcpm")
    private String guaranteedCPMForOther;

    @JsonProperty("minimum_requests")
    private Long minimumRequests;

    @JsonProperty("minimum_payout")
    private Integer minimumPayout;

    protected RevenueWebModel(){

    }

    public RevenueWebModel(RevenueModel revenueModel){

        setId(revenueModel.getId());

        setRevenueModelType(revenueModel.getRevenueModelType());

        if (revenueModel.getRevenueSharePercentage() != null)
        {
            setRevenueSharePercentage(revenueModel.getRevenueSharePercentage().toString());
        }

        if (revenueModel.getRevenueModelType() == PublisherRevenueModelType.GUARANTEED_CPM || revenueModel.getRevenueModelType() == PublisherRevenueModelType.GUARANTEED_CPM_WITH_FILL)
        {

            if (revenueModel.getGuaranteedCPMForUSDesktopImpressions() != null)
            {
                int dollarForUSDesktop = revenueModel.getGuaranteedCPMForUSDesktopImpressions() / 100;
                int centsForUSDesktop = revenueModel.getGuaranteedCPMForUSDesktopImpressions() - (dollarForUSDesktop * 100);

                if (centsForUSDesktop >= 10)
                {
                    setGuaranteedCPMForUSDesktop(dollarForUSDesktop + "." + centsForUSDesktop);
                }
                else
                {
                    setGuaranteedCPMForUSDesktop(dollarForUSDesktop + ".0" + centsForUSDesktop);
                }
            }

            if (revenueModel.getGuaranteedCPMForOtherImpressions() != null)
            {
                int dollarForOther = revenueModel.getGuaranteedCPMForOtherImpressions() / 100;
                int centsForOther = revenueModel.getGuaranteedCPMForOtherImpressions() - (dollarForOther * 100);

                if (centsForOther >= 10)
                {
                    setGuaranteedCPMForOther(dollarForOther + "." + centsForOther);
                }
                else
                {
                    setGuaranteedCPMForOther(dollarForOther + ".0" + centsForOther);
                }
            }
        }

        if (revenueModel.getMinimumRevenueInCents() != null)
        {
            setMinimumRequests(revenueModel.getMinimumImpressions());
            setMinimumPayout(revenueModel.getMinimumRevenueInCents() / 100);
        }

        setEffectiveStartDate(revenueModel.getStartDate());
        setEffectiveEndDate(revenueModel.getEndDate());

    }

    public PublisherRevenueModelType getRevenueModelType() {
        return revenueModelType;
    }

    public void setRevenueModelType(PublisherRevenueModelType revenueModelType) {
        this.revenueModelType = revenueModelType;
    }

    public String getRevenueSharePercentage() {
        return revenueSharePercentage;
    }

    public void setRevenueSharePercentage(String revenueSharePercentage) {
        this.revenueSharePercentage = revenueSharePercentage;
    }

    public String getGuaranteedCPMForUSDesktop() {
        return guaranteedCPMForUSDesktop;
    }

    public void setGuaranteedCPMForUSDesktop(String guaranteedCPMForUSDesktop) {
        this.guaranteedCPMForUSDesktop = guaranteedCPMForUSDesktop;
    }

    public String getGuaranteedCPMForOther() {
        return guaranteedCPMForOther;
    }

    public void setGuaranteedCPMForOther(String guaranteedCPMForOther) {
        this.guaranteedCPMForOther = guaranteedCPMForOther;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public Long getMinimumRequests() {
        return minimumRequests;
    }

    public void setMinimumRequests(Long minimumRequests) {
        this.minimumRequests = minimumRequests;
    }

    public Integer getMinimumPayout() {
        return minimumPayout;
    }

    public void setMinimumPayout(Integer minimumPayout) {
        this.minimumPayout = minimumPayout;
    }

    public ProductFormat getFormat() {
        return format;
    }

    public void setFormat(ProductFormat format) {
        this.format = format;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
