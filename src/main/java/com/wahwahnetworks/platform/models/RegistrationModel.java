package com.wahwahnetworks.platform.models;

/**
 * Created by jhaygood on 3/4/16.
 */
public class RegistrationModel {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String siteName;
    private String siteURL;
    private String trafficEstimate;
    private boolean acceptTerms;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getTrafficEstimate() {
        return trafficEstimate;
    }

    public void setTrafficEstimate(String trafficEstimate) {
        this.trafficEstimate = trafficEstimate;
    }

    public boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }
}
