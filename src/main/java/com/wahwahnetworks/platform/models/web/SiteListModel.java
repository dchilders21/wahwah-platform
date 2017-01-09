package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Site;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@WebSafeModel
public class SiteListModel
{
	private List<SiteModel> sites = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;

	private int numberOfPages;

	private int highlightOffset;
	private int highlightId;

	public SiteListModel(Page<Site> sites, Boolean isSuperUser)
	{
		for (Site site : sites)
		{
			// Non super-user shouldn't see internal notes. See service for more information
			if (isSuperUser)
				this.add(new SiteInternalModel(site));
			else
				this.add(new SiteModel(site));
		}

		setCurrentPage(sites.getNumber());
		setCurrentPageSize(sites.getNumberOfElements());
		setNumberOfPages(sites.getTotalPages());
	}

	public void add(SiteModel site)
	{
		sites.add(site);
	}

	@JsonProperty("sites")
	public List<SiteModel> getSites()
	{
		return sites;
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

	@JsonProperty("highlight_offset")
	public int getHighlightOffset()
	{
		return highlightOffset;
	}

	public void setHighlightOffset(int highlightOffset)
	{
		this.highlightOffset = highlightOffset;
	}

	@JsonProperty("highlight_id")
	public int getHighlightId()
	{
		return highlightId;
	}

	public void setHighlightId(int highlightId)
	{
		this.highlightId = highlightId;
	}
}
