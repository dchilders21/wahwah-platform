package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Brian.Bober on 2/8/2015.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SiteType
{
	ENTERTAINMENT, WOMENS_LIFESTYLE, MENS_LIFESTYLE, NEWS_AND_BUSINESS, SPORTS
}
