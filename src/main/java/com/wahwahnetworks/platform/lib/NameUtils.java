package com.wahwahnetworks.platform.lib;

/**
 * Created by Brian.Bober on 3/31/2016.
 */
public class NameUtils
{
	// DO NOT CHANGE mpSuffix and pSuffix.
    // Changing these values could cause problems for older sites/publishers/networks, so don't
    // Will need to write flyway script to make change
	static public final String mpSuffix = " - MP"; // Marketplace suffix
	static public final String pSuffix = " - P"; // Publisher non-marketplace suffix
	static public String modifyNameWithPrependAndSuffix(String origString, Integer maxLen, String prepend, String suffix )
	{
		if (prepend.length() + suffix.length() >= maxLen) // Avoid silly errors
			return origString;
		// Preserve " - MP" and " - P"
		String preserveMPP = "";
		if (origString.endsWith(NameUtils.mpSuffix))
		{
			preserveMPP = NameUtils.mpSuffix;
		}
		else
		if (origString.endsWith(NameUtils.pSuffix))
		{
			preserveMPP = NameUtils.pSuffix;
		}
		origString = origString.replaceAll(preserveMPP, "");
		Integer truncateToChars = maxLen - prepend.length() - suffix.length() - preserveMPP.length();
		String newString = new String((origString.length() <= truncateToChars)? origString : origString.substring(0,truncateToChars-1));
		return prepend + newString + suffix + preserveMPP;
	}
}
