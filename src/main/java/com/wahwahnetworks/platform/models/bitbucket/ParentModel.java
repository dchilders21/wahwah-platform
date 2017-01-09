package com.wahwahnetworks.platform.models.bitbucket;

/**
 * Created by Justin on 5/19/2014.
 */
public class ParentModel
{
	private String full_name;
	private String name;
	private ParentLinkModel links;

	public String getFull_name()
	{
		return full_name;
	}

	public void setFull_name(String full_name)
	{
		this.full_name = full_name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ParentLinkModel getLinks()
	{
		return links;
	}

	public void setLinks(ParentLinkModel links)
	{
		this.links = links;
	}

	public class ParentLinkModel
	{
		private LinkModel self;
		private LinkModel avatar;

		public LinkModel getSelf()
		{
			return self;
		}

		public void setSelf(LinkModel self)
		{
			this.self = self;
		}

		public LinkModel getAvatar()
		{
			return avatar;
		}

		public void setAvatar(LinkModel avatar)
		{
			this.avatar = avatar;
		}
	}
}
