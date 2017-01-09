package com.wahwahnetworks.platform.models.bitbucket;

import java.util.List;

/**
 * Created by Justin on 5/19/2014.
 */
public class RepositoryLinksModel
{
	private LinkModel watchers;
	private LinkModel commits;
	private LinkModel self;
	private LinkModel html;
	private LinkModel avatar;
	private LinkModel forks;
	private List<NamedLinkModel> clone;
	private LinkModel pullrequests;

	public LinkModel getWatchers()
	{
		return watchers;
	}

	public void setWatchers(LinkModel watchers)
	{
		this.watchers = watchers;
	}

	public LinkModel getCommits()
	{
		return commits;
	}

	public void setCommits(LinkModel commits)
	{
		this.commits = commits;
	}

	public LinkModel getSelf()
	{
		return self;
	}

	public void setSelf(LinkModel self)
	{
		this.self = self;
	}

	public LinkModel getHtml()
	{
		return html;
	}

	public void setHtml(LinkModel html)
	{
		this.html = html;
	}

	public LinkModel getAvatar()
	{
		return avatar;
	}

	public void setAvatar(LinkModel avatar)
	{
		this.avatar = avatar;
	}

	public LinkModel getForks()
	{
		return forks;
	}

	public void setForks(LinkModel forks)
	{
		this.forks = forks;
	}

	public List<NamedLinkModel> getClone()
	{
		return clone;
	}

	public void setClone(List<NamedLinkModel> clone)
	{
		this.clone = clone;
	}

	public LinkModel getPullrequests()
	{
		return pullrequests;
	}

	public void setPullrequests(LinkModel pullrequests)
	{
		this.pullrequests = pullrequests;
	}
}
