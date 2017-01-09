package com.wahwahnetworks.platform.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 2/23/15.
 */
public class SupportMessage
{
	@JsonProperty("subject")
	private
	String subject;

	@JsonProperty("message_body")
	private
	String body;

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}
}
