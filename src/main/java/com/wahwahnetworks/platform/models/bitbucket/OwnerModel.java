package com.wahwahnetworks.platform.models.bitbucket;

/**
 * Created by Justin on 5/19/2014.
 */
public class OwnerModel
{
	private String username;
	private String display_name;
	private OwnerLinkModel links;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getDisplay_name()
	{
		return display_name;
	}

	public void setDisplay_name(String display_name)
	{
		this.display_name = display_name;
	}

	public OwnerLinkModel getLinks()
	{
		return links;
	}

	public void setLinks(OwnerLinkModel links)
	{
		this.links = links;
	}

	public class OwnerLinkModel
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
