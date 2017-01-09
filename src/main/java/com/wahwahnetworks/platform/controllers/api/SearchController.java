package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.models.web.SearchResultsModel;
import com.wahwahnetworks.platform.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Brian.Bober on 5/26/2015.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/search")
public class SearchController extends BaseAPIController
{

	@Autowired
	SearchService searchService;

	@RequestMapping(value = "/{query}", method = RequestMethod.GET, produces = "application/json")
	public SearchResultsModel search(@PathVariable String query)
	{
		return searchService.search(query);
	}
}
