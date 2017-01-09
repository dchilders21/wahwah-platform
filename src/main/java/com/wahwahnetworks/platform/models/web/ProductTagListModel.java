package com.wahwahnetworks.platform.models.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 7/14/2015.
 */

// Intentionally not websafe. Shouldn't be sending out
public class ProductTagListModel
{

	public ProductTagListModel()
	{
		tags = new ArrayList<>();
	}

	List<ProductTagListEntry> tags;

	public List<ProductTagListEntry> getTags()
	{
		return tags;
	}

	public void addTag(ProductTagListEntry entry){
		tags.add(entry);
	}
}
