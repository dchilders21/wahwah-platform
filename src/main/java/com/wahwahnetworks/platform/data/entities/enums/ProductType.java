package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Created by Brian.Bober on 12/21/2014.
 */


@JsonFormat(shape = Shape.STRING)
public enum ProductType
{
	TOOLBAR, MINI_BAR, CUSTOM, STANDALONE_AD, AD_SERVER_NATIVE /* ad only */
}
