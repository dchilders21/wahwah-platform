package com.wahwahnetworks.platform.config;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by jhaygood on 3/2/15.
 */

@Component
public class SecurityConfiguration
{

	private static final Logger logger = Logger.getLogger(SecurityConfiguration.class);

	public SecurityConfiguration()
	{
		removeCryptographyRestrictions();
	}

	private static void removeCryptographyRestrictions()
	{
		if (!isRestrictedCryptography())
		{
			return;
		}
		try
		{
			java.lang.reflect.Field isRestricted;
			try
			{
				final Class<?> c = Class.forName("javax.crypto.JceSecurity");
				isRestricted = c.getDeclaredField("isRestricted");
			}
			catch (final ClassNotFoundException e)
			{
				try
				{
					// Java 6 has obfuscated JCE classes
					final Class<?> c = Class.forName("javax.crypto.SunJCE_b");
					isRestricted = c.getDeclaredField("g");
				}
				catch (final ClassNotFoundException e2)
				{
					throw e;
				}
			}
			isRestricted.setAccessible(true);
			isRestricted.set(null, false);
		}
		catch (final Throwable e)
		{
			logger.warn("Failed to remove cryptography restrictions", e);
		}
	}

	private static boolean isRestrictedCryptography()
	{
		return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}
}
