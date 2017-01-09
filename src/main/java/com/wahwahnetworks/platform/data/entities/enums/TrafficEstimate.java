package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Brian.Bober on 2/9/2015.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TrafficEstimate
{
	_0_TO_1MM, _1MM_TO_5MM, _5MM_TO_10MM, _10MM_TO_20MM, _20MM_PLUS

}

