package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
public class PublisherWebListModel
{

	private List<PublisherWebModel> publisherWebModelList = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;
	private int numberOfPages;


	public PublisherWebListModel(List<PublisherWebModel> accountModels)
	{
		publisherWebModelList.addAll(accountModels);
	}

	public void setPublisherWebListModelAccounts(List<AccountPublisher> accounts)
	{

		publisherWebModelList = new ArrayList<>();
		for (AccountPublisher account : accounts)
		{
			add(new PublisherInternalWebModel(account, null /* no red panda publisher in list for now */, 0));
		}

		setCurrentPage(0);
		setCurrentPageSize(this.publisherWebModelList.size());
		setNumberOfPages(1);
	}

	public PublisherWebListModel(Page<AccountPublisher> accounts, AccountPublisher redPandaPublisherCreator)
	{
		for (AccountPublisher account : accounts)
		{
			// Todo: Ensure access to each account for sessionModel.getAccount()
			if (redPandaPublisherCreator == null )
			{
				add(new PublisherInternalWebModel(account, null, 0));
			}
			else
			{
				add(new PublisherWebModel(account, redPandaPublisherCreator, 0));
			}
		}

		setCurrentPage(accounts.getNumber());
		setCurrentPageSize(accounts.getNumberOfElements());
		setNumberOfPages(accounts.getTotalPages());
	}

	private void add(PublisherWebModel accountModel)
	{
		publisherWebModelList.add(accountModel);
	}

	@JsonProperty("publishers")
	public List<PublisherWebModel> getPublishers()
	{
		return publisherWebModelList;
	}

	@JsonProperty("page_current")
	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	@JsonProperty("page_size")
	public int getCurrentPageSize()
	{
		return currentPageSize;
	}

	public void setCurrentPageSize(int currentPageSize)
	{
		this.currentPageSize = currentPageSize;
	}

	@JsonProperty("page_totalcount")
	public int getNumberOfPages()
	{
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages)
	{
		this.numberOfPages = numberOfPages;
	}
}
