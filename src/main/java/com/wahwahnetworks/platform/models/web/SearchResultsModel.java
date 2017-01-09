package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 5/26/2015.
 */

@WebSafeModel
public class SearchResultsModel
{
	@JsonProperty("publishers")
	private List<PublisherWebModel> publishers;

	@JsonProperty("sites")
	private List<SiteModel> sites;

	@JsonProperty("products")
	private List<ProductModel> products;

	@JsonProperty("networks")
	private List<NetworkWebModel> networks;

	public SearchResultsModel()
	{
		publishers = new ArrayList<PublisherWebModel>();
		sites = new ArrayList<SiteModel>();
		products = new ArrayList<ProductModel>();
		networks = new ArrayList<NetworkWebModel>();

	}

	public List<PublisherWebModel> getPublishers()
	{
		return publishers;
	}

	public void setPublishers(List<PublisherWebModel> publishers)
	{
		this.publishers = publishers;
	}

	public List<SiteModel> getSites()
	{
		return sites;
	}

	public void setSites(List<SiteModel> sites)
	{
		this.sites = sites;
	}

	public List<ProductModel> getProducts()
	{
		return products;
	}

	public void setProducts(List<ProductModel> products)
	{
		this.products = products;
	}

	public List<NetworkWebModel> getNetworks()
	{
		return networks;
	}

	public void setNetworks(List<NetworkWebModel> networks)
	{
		this.networks = networks;
	}

	@JsonProperty("total_count")
	public int getTotalCount()
	{
		return networks.size() + publishers.size() + sites.size() + products.size();
	}
}
