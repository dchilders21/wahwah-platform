package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by Justin on 2/2/2015.
 */

@WebSafeModel
public class IntegerWebModel
{
	private int value;

	public IntegerWebModel(int value)
	{
		setValue(value);
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}
}
