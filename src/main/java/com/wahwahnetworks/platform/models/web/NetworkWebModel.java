package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
		@JsonSubTypes.Type(value = NetworkInternalWebModel.class, name = "InternalAccount")
)
public class NetworkWebModel extends AccountWebModel
{

    @JsonProperty("is_single_publisher")
    private boolean isSinglePublisher;

    @JsonProperty("single_publisher_id")
    private Integer singlePublisherId;

    @JsonProperty("default_publisher_id")
    private Integer defaultPublisherId;

    @JsonProperty("single_publisher_name")
    private String singlePublisherName;

    @JsonProperty("publisher_count") // Does NOT include default pub
    private int currentPublisherCount;

    @JsonProperty("default_product_format")
    private ProductFormat defaultProductFormat;

    @JsonProperty("passback_display_tag_html")
    private String passbackDisplayTagHtml;


    public NetworkWebModel()
	{

	}
	public NetworkWebModel(AccountNetwork account)
	{
		super(account);
        setSinglePublisher(account.isSinglePublisher());
        setDefaultProductFormat(account.getDefaultProductFormat());
        setPassbackDisplayTagHtml(account.getPassbackDisplayTagHtml());

        if(isSinglePublisher) {
            setSinglePublisherId(account.getSinglePublisher().getId());
            setSinglePublisherName(account.getSinglePublisher().getName());
        }

        currentPublisherCount = 0;
        defaultPublisherId = null;
        singlePublisherId = null;

        for (Account p :  account.getChildAccounts()) // Set up to handle future alternate acct types
        {
            if (p.getAccountType() == AccountType.PUBLISHER)
            {
                AccountPublisher publisher = (AccountPublisher) p;
                if (!publisher.isDefault())
                {
                    currentPublisherCount++;
                }
                else
                {
                    defaultPublisherId = publisher.getId();
                }
            }
        }

        if(currentPublisherCount == 1){
            setSinglePublisherId(account.getChildAccounts().get(0).getId());
            setSinglePublisherName(account.getChildAccounts().get(0).getName());
        }
	}


    public boolean isSinglePublisher() {
        return isSinglePublisher;
    }

    public void setSinglePublisher(boolean singlePublisher) {
        isSinglePublisher = singlePublisher;
    }

    public Integer getSinglePublisherId() {
        return singlePublisherId;
    }

    public void setSinglePublisherId(Integer singlePublisherId) {
        this.singlePublisherId = singlePublisherId;
    }

    public int getCurrentPublisherCount() {
        return currentPublisherCount;
    }

    public void setCurrentPublisherCount(int currentPublisherCount) {
        this.currentPublisherCount = currentPublisherCount;
    }

    public String getSinglePublisherName() {
        return singlePublisherName;
    }

    public void setSinglePublisherName(String singlePublisherName) {
        this.singlePublisherName = singlePublisherName;
    }


    public ProductFormat getDefaultProductFormat()
    {
        return defaultProductFormat;
    }

    public void setDefaultProductFormat(ProductFormat defaultProductFormat)
    {
        this.defaultProductFormat = defaultProductFormat;
    }

    public Integer getDefaultPublisherId()
    {
        return defaultPublisherId;
    }

    public void setDefaultPublisherId(Integer defaultPublisherId)
    {
        this.defaultPublisherId = defaultPublisherId;
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
