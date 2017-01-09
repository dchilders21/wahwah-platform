package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 5/10/2016.
 */

// Note: A convenience enum for now
@JsonFormat(shape = Shape.STRING)
public enum ProductFormat
{
	OUTSTREAM, FLOATER, BANNER, CUSTOM, ADSERVERNATIVE
}
