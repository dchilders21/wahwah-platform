package com.wahwahnetworks.platform.models.edgecast;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jhaygood on 2/21/15.
 */
public class EdgeCastPurgeRequest
{
	@JsonProperty("MediaPath")
	private String mediaPath;

	@JsonProperty("MediaType")
	private int mediaType;

	public String getMediaPath()
	{
		return mediaPath;
	}

	public void setMediaPath(String value)
	{
		mediaPath = value;
	}

	@JsonIgnore
	public EdgeCastMediaType getMediaType()
	{
		switch (mediaType)
		{
			case 2:
				return EdgeCastMediaType.FLASH_MEDIA_STREAMING;
			case 3:
				return EdgeCastMediaType.HTTP_LARGE;
			case 8:
				return EdgeCastMediaType.HTTP_SMALL;
			case 14:
				return EdgeCastMediaType.APPLICATION_DELIVERY_NETWORK;
		}

		throw new RuntimeException("Unknown Media Type");
	}

	@JsonIgnore
	public void setMediaType(EdgeCastMediaType ecMediaType)
	{
		switch (ecMediaType)
		{
			case FLASH_MEDIA_STREAMING:
				mediaType = 2;
				break;
			case HTTP_LARGE:
				mediaType = 3;
				break;
			case HTTP_SMALL:
				mediaType = 8;
				break;
			case APPLICATION_DELIVERY_NETWORK:
				mediaType = 14;
				break;
		}
	}
}
