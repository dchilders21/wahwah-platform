package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 12/21/2014.
 */

/* For ad-only standalone ads */
@JsonFormat(shape = Shape.STRING)
public enum StandaloneAdFormat
{
	banner, floater, ostream
}
