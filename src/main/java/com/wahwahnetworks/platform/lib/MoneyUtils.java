package com.wahwahnetworks.platform.lib;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by jhaygood on 6/13/16.
 */
public class MoneyUtils {
    public static Integer getCentsForDollarString(String dollarValue)
    {
        if(StringUtils.isEmpty(dollarValue)){
            return null;
        }

        if (!dollarValue.contains("."))
        {
            // No Cents
            return Integer.parseInt(dollarValue) * 100;
        }
        else
        {
            int cents;

            String[] dollarValueParts = dollarValue.split(Pattern.quote("."));
            cents = Integer.parseInt(dollarValueParts[1]);

            if(!StringUtils.isEmpty(dollarValueParts[0])){
                cents += (Integer.parseInt(dollarValueParts[0]) * 100);
            }

            return cents;
        }

    }

    public static String getDollarStringForCents(int cents){
        int dollars = cents / 100;
        int centsPart = cents - (dollars * 100);

        if(centsPart >= 10){
            return dollars + "." + centsPart;
        } else {
            return dollars + ".0" + centsPart;
        }

    }
}
