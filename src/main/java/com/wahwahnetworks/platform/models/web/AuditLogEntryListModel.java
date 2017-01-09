package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.AuditLogEntry;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Justin on 1/26/2015.
 */

@WebSafeModel
public class AuditLogEntryListModel
{
	private List<AuditLogEntryModel> auditLogEntries;

	private int currentPage;
	private int currentPageSize;

	private int numberOfPages;

	public AuditLogEntryListModel(List<AuditLogEntryModel> entries)
	{
		auditLogEntries = entries;

		setCurrentPage(0);
		setCurrentPageSize(this.auditLogEntries.size());
		setNumberOfPages(1);
	}

	public AuditLogEntryListModel(List<AuditLogEntryModel> entries, Page<AuditLogEntry> page)
	{
		auditLogEntries = entries;

		setCurrentPage(page.getNumber());
		setCurrentPageSize(page.getNumberOfElements());
		setNumberOfPages(page.getTotalPages());
	}

	@JsonProperty("log_entries")
	public List<AuditLogEntryModel> getAuditLogEntries()
	{
		return auditLogEntries;
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
