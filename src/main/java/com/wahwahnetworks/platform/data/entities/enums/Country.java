package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 2/9/2015.
 */

@JsonFormat(shape = Shape.STRING)
public enum Country
{
	UNITED_STATES, SPAIN, BRAZIL
}
