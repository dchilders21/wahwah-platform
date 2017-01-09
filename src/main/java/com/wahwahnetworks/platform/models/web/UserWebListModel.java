package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.models.UserModel;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 6/8/2014.
 */

@WebSafeModel
public class UserWebListModel
{
	private List<UserWebModel> userWebModelList = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;

	private int numberOfPages;

	public UserWebListModel(Iterable<UserModel> userModels)
	{
		for (UserModel userModel : userModels)
		{
			UserWebModel userWebModel = new UserWebModel(userModel);
			userWebModelList.add(userWebModel);
		}

		setCurrentPage(0);
		setCurrentPageSize(this.userWebModelList.size());
		setNumberOfPages(1);
	}

	public UserWebListModel(List<UserWebModel> webModels)
	{
		userWebModelList.addAll(webModels);

		setCurrentPage(0);
		setCurrentPageSize(this.userWebModelList.size());
		setNumberOfPages(1);
	}

	public UserWebListModel(Page<User> users)
	{
		for (User user : users)
		{
			UserWebModel userWebModel = new UserWebModel(user);
			userWebModelList.add(userWebModel);
		}

		setCurrentPage(users.getNumber());
		setCurrentPageSize(users.getNumberOfElements());
		setNumberOfPages(users.getTotalPages());
	}

	@JsonProperty("users")
	public List<UserWebModel> getUsers()
	{
		return userWebModelList;
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
