package com.wahwahnetworks.platform.lib;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by jhaygood on 12/15/15.
 */
public class UrlHelper {
    public static String getQuery(Collection<Map.Entry<String,String>> params) throws UnsupportedEncodingException
    {
        List<Pair<String,String>> paramsPairList = new ArrayList<>();

        for (Map.Entry<String,String> pair : params)
        {
            Pair<String,String> paramPair = new ImmutablePair<>(pair.getKey(),pair.getValue());
            paramsPairList.add(paramPair);
        }

        return getQueryForParams(paramsPairList);
    }

    public static String getQueryForParams(Collection<Pair<String,String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String,String> pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String getQueryForMap(Map<String,String> params) throws UnsupportedEncodingException {
        return getQuery(params.entrySet());
    }

    public static Map<String,String> parseQuery(String query){

        URI uri = URI.create("http://localhost/?" + query);

        UriComponents components = UriComponentsBuilder.fromUri(uri).build();
        return components.getQueryParams().toSingleValueMap();
    }

    public static String getHostName(String url){
        UriComponents components = UriComponentsBuilder.fromUriString(url).build();
        return components.getHost();
    }
}
