package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Brian.Bober on 4/14/2016.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum LineItemTargetingRuleDisplayType
{
	FIRST_LOOK, BACKUP
}
