package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Brian.Bober on 5/8/2015.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Environment
{
	PRODUCTION, STAGING, OTHER
}
