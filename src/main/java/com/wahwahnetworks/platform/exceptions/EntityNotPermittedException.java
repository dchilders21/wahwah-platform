package com.wahwahnetworks.platform.exceptions;

/**
 * Created by Justin on 6/8/2014.
 */
public class EntityNotPermittedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public EntityNotPermittedException(String message)
	{
		super(message);
	}
}
