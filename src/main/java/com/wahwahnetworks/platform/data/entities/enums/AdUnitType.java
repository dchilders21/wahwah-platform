package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by jhaygood on 4/9/15.
 */

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AdUnitType
{
	DISPLAY, MOBILE, VIDEO
}
