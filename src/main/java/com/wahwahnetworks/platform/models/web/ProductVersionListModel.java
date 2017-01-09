package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

import java.util.List;

/**
 * Created by Justin on 2/6/2015.
 */

@WebSafeModel
public class ProductVersionListModel
{
	private List<ProductVersionWebModel> productVersionWebModels;

	public ProductVersionListModel(List<ProductVersionWebModel> webModels)
	{
		productVersionWebModels = webModels;
	}

	public List<ProductVersionWebModel> getProductVersions()
	{
		return productVersionWebModels;
	}
}
