package com.khan.tickets.demo.model.converters;

import com.khan.tickets.demo.model.TicketType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class TicketTypeConverter implements AttributeConverter<TicketType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TicketType type) {
        if(type == null) {
            return null;
        }

        return type.getId();
    }

    @Override
    public TicketType convertToEntityAttribute(Integer id) {
        if(id == null) {
            return null;
        }

        return Stream.of(TicketType.values()).filter(t -> t.getId() == id).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
