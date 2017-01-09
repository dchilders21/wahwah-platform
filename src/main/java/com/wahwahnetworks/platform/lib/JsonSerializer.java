package com.wahwahnetworks.platform.lib;

import com.fasterxml.jackson.core.JsonFactory;

import java.io.IOException;

/**
 * Created by jhaygood on 4/11/16.
 */
public class JsonSerializer extends JacksonSerializer {
    public static String serialize(Object object) throws IOException {
        byte[] data = serialize(object,new JsonFactory());
        return new String(data, "UTF-8");
    }

    public static <T> T deserialize(String json, Class<T> classType) throws IOException {
        return deserialize(json.getBytes("UTF-8"),classType,new JsonFactory());
    }
}