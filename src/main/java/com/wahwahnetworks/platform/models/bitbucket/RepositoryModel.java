package com.wahwahnetworks.platform.models.bitbucket;

/**
 * Created by Justin on 5/18/2014.
 */
public class RepositoryModel
{
	private String scm;
	private String has_wiki;
	private String description;
	private String name;
	private RepositoryLinksModel links;
	private String fork_policy;
	private String language;
	private String created_on;
	private ParentModel parent;
	private String full_name;
	private boolean has_issues;
	private OwnerModel owner;
	private String updated_on;
	private long size;
	private boolean is_private;

	public String getScm()
	{
		return scm;
	}

	public void setScm(String scm)
	{
		this.scm = scm;
	}

	public String getHas_wiki()
	{
		return has_wiki;
	}

	public void setHas_wiki(String has_wiki)
	{
		this.has_wiki = has_wiki;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public RepositoryLinksModel getLinks()
	{
		return links;
	}

	public void setLinks(RepositoryLinksModel links)
	{
		this.links = links;
	}

	public String getFork_policy()
	{
		return fork_policy;
	}

	public void setFork_policy(String fork_policy)
	{
		this.fork_policy = fork_policy;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getCreated_on()
	{
		return created_on;
	}

	public void setCreated_on(String created_on)
	{
		this.created_on = created_on;
	}

	public ParentModel getParent()
	{
		return parent;
	}

	public void setParent(ParentModel parent)
	{
		this.parent = parent;
	}

	public String getFull_name()
	{
		return full_name;
	}

	public void setFull_name(String full_name)
	{
		this.full_name = full_name;
	}

	public boolean isHas_issues()
	{
		return has_issues;
	}

	public void setHas_issues(boolean has_issues)
	{
		this.has_issues = has_issues;
	}

	public OwnerModel getOwner()
	{
		return owner;
	}

	public void setOwner(OwnerModel owner)
	{
		this.owner = owner;
	}

	public String getUpdated_on()
	{
		return updated_on;
	}

	public void setUpdated_on(String updated_on)
	{
		this.updated_on = updated_on;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public boolean isIs_private()
	{
		return is_private;
	}

	public void setIs_private(boolean is_private)
	{
		this.is_private = is_private;
	}
}
