package com.wahwahnetworks.platform.lib.openx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

/**
 * Created by Brian.Bober on 2/11/2016.
 */

// Use with Pair<name,value>
public class CustomNameValueSerializer extends JsonSerializer<Pair<Object,Object>>
{
	public void serialize(Pair<Object,Object> data, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeObjectField(data.getKey().toString(), data.getValue().toString());
		jgen.writeEndObject();
	}

}
