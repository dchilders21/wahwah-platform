package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 1/15/2015.
 */


@JsonFormat(shape = Shape.STRING)
public enum WWLogLevel
{
	error, debug, info, warn
}
