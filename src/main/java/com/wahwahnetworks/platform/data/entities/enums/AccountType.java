package com.wahwahnetworks.platform.data.entities.enums;

/**
 * Created by Brian.Bober on 1/1/2015.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(shape = Shape.STRING)
public enum AccountType
{
	ROOT,
	PUBLISHER,
	NETWORK,
	ADVERTISER,
	FREE,
	REPORTING_PRO
}
