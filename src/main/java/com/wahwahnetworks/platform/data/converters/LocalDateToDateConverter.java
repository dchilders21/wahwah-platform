package com.wahwahnetworks.platform.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by jhaygood on 4/6/16.
 */

@Converter(autoApply = true)
public class LocalDateToDateConverter implements AttributeConverter<LocalDate, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        if(attribute != null){
            return Date.valueOf(attribute);
        } else {
            return null;
        }
    }

    @Override
    public LocalDate convertToEntityAttribute(Date dbData) {

        if(dbData != null){
            return dbData.toLocalDate();
        } else {
            return null;
        }

    }
}
