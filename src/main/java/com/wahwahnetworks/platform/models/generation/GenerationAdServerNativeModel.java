package com.wahwahnetworks.platform.models.generation;

/**
 * Created by jhaygood on 5/18/16.
 */
public class GenerationAdServerNativeModel {

    private int id;


    /* Info comments */
    private String productName;
    private String siteName;
    private Integer productId;
    private String publishDate;

    private String platformCommitId;
    private String platformVersion;

    private String nativeTagContents;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getNativeTagContents() {
        return nativeTagContents;
    }

    public void setNativeTagContents(String nativeTagContents) {
        this.nativeTagContents = nativeTagContents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformCommitId() {
        return platformCommitId;
    }

    public void setPlatformCommitId(String platformCommitId) {
        this.platformCommitId = platformCommitId;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }
}
