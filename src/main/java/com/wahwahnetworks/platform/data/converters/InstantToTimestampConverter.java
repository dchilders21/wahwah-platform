package com.wahwahnetworks.platform.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by jhaygood on 2/21/16.
 */

@Converter(autoApply = true)
public class InstantToTimestampConverter implements AttributeConverter<Instant, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(Instant attribute) {
        if (attribute == null)
            return null;
        return Timestamp.from(attribute);
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null)
            return null;
        return dbData.toInstant();
    }
}
