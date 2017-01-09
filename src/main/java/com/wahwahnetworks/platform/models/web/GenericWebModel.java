package com.wahwahnetworks.platform.models.web;

import com.wahwahnetworks.platform.annotations.WebSafeModel;

/**
 * Created by Justin on 2/2/2015.
 */

@WebSafeModel
public class GenericWebModel<T>
{
	private T model;

	public T getModel()
	{
		return model;
	}

	public void setModel(T model)
	{
		this.model = model;
	}
}
