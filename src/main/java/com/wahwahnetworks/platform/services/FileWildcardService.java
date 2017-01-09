package com.wahwahnetworks.platform.services;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jhaygood on 2/25/15.
 */

@Service
public class FileWildcardService
{

	@Autowired
	private ApplicationContext applicationContext;

	private HashMap<String, List<String>> listOfWildcards;

	public FileWildcardService()
	{
		listOfWildcards = new HashMap<>();
	}

	public boolean doesFileNameMatchList(String fileName, String listName) throws IOException
	{
		List<String> wildcardList = getWildcardList(listName);

		boolean doesMatch = false;

		for (String wildcard : wildcardList)
		{
			if (FilenameUtils.wildcardMatch(fileName, wildcard))
			{
				doesMatch = true;
			}
		}

		return doesMatch;
	}

	private List<String> getWildcardList(String listName) throws IOException
	{
		if (!listOfWildcards.containsKey(listName))
		{
			Resource wildcardListResource = applicationContext.getResource("classpath:data/" + listName + ".txt");
			List<String> wildcardList = IOUtils.readLines(wildcardListResource.getInputStream(), Charset.forName("UTF-8"));
			listOfWildcards.put(listName, wildcardList);
		}

		return listOfWildcards.get(listName);
	}
}
