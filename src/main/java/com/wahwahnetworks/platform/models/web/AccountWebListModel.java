package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.UserModel;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 6/8/2014.
 */

@WebSafeModel
public class AccountWebListModel
{

	private List<AccountWebModel> accountWebModelList = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;
	private int numberOfPages;

	public AccountWebListModel(List<AccountWebModel> accountModels)
	{
		accountWebModelList.addAll(accountModels);
	}

	public AccountWebListModel(List<Account> accounts, Account parentAccount)
	{
		for (Account account : accounts)
		{
			if (parentAccount == null || parentAccount.getAccountType() == AccountType.NETWORK)
			{
				add(new AccountInternalWebModel(account));
			}
			else
			{
				add(new AccountWebModel(account));
			}
		}

		setCurrentPage(0);
		setCurrentPageSize(this.accountWebModelList.size());
		setNumberOfPages(1);
	}

	public AccountWebListModel(Page<? extends Account> accounts, Account parentAccount)
	{
		for (Account account : accounts)
		{
			// Todo: Ensure access to each account for sessionModel.getAccount()
			if (parentAccount == null )
			{
				add(new AccountInternalWebModel(account));
			}
			else
			{
				add(new AccountWebModel(account));
			}
		}

		setCurrentPage(accounts.getNumber());
		setCurrentPageSize(accounts.getNumberOfElements());
		setNumberOfPages(accounts.getTotalPages());
	}

	private void add(AccountWebModel accountModel)
	{
		accountWebModelList.add(accountModel);
	}

	@JsonProperty("publishers")
	public List<AccountWebModel> getPublishers()
	{
		return accountWebModelList;
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
