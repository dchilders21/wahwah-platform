package com.wahwahnetworks.platform.data.entities.enums.liverail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 11/24/2015.
 */

@JsonFormat(shape = Shape.STRING)
public enum LiverailStatus
{
	success, fail
}
