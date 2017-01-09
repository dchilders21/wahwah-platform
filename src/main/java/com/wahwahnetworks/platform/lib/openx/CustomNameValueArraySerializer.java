package com.wahwahnetworks.platform.lib.openx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Brian.Bober on 2/11/2016.
 */

// Use with ArrayList<Pair<Object,Object>>
public class CustomNameValueArraySerializer extends JsonSerializer<ArrayList<Pair<Object,Object>>>
{
	public void serialize(ArrayList<Pair<Object,Object>> array, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
	{
		jgen.writeStartObject();
		for (int i =0; i < array.size(); i++)
		{
			jgen.writeObjectField(array.get(i).getKey().toString(), array.get(i).getValue().toString());
		}
		jgen.writeEndObject();
	}

}
