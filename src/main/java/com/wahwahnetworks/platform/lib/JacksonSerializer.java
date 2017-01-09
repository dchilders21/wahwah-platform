package com.wahwahnetworks.platform.lib;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jhaygood on 4/11/16.
 */
public class JacksonSerializer {
    protected static byte[] serialize(Object object, JsonFactory jsonFactory) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        mapper.writeValue(byteArrayOutputStream,object);
        return byteArrayOutputStream.toByteArray();
    }

    protected static <T> T deserialize(byte[] byteArray, Class<T> classType, JsonFactory jsonFactory) throws IOException {
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        T instance  = mapper.readValue(byteArray,classType);
        return instance;
    }
}
