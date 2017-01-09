package com.wahwahnetworks.platform.exceptions;

public class ModelValidationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ModelValidationException(String message)
	{
		super(message);
	}
}
