package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 1/29/2016.
 */

@WebSafeModel
public class NetworkWebListModel
{

	private List<NetworkWebModel> networkWebModelList = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;
	private int numberOfPages;

	public NetworkWebListModel(List<NetworkWebModel> accountModels)
	{
		networkWebModelList.addAll(accountModels);
	}

	public NetworkWebListModel(List<AccountNetwork> accounts, Account parentAccount)
	{
		for (AccountNetwork account : accounts)
		{
			if (parentAccount == null || parentAccount.getAccountType() == AccountType.NETWORK)
			{
				add(new NetworkInternalWebModel(account));
			}
			else
			{
				add(new NetworkWebModel(account));
			}
		}

		setCurrentPage(0);
		setCurrentPageSize(this.networkWebModelList.size());
		setNumberOfPages(1);
	}

	public NetworkWebListModel(Page<AccountNetwork> accounts)
	{
		for (AccountNetwork account : accounts)
		{
			// Todo: Ensure access to each account for sessionModel.getAccount()
			add(new NetworkInternalWebModel(account));
		}

		setCurrentPage(accounts.getNumber());
		setCurrentPageSize(accounts.getNumberOfElements());
		setNumberOfPages(accounts.getTotalPages());
	}

	private void add(NetworkWebModel accountModel)
	{
		networkWebModelList.add(accountModel);
	}

	@JsonProperty("networks")
	public List<NetworkWebModel> getNetworks()
	{
		return networkWebModelList;
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
