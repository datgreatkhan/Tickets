package com.khan.tickets.demo.model.converters;

import com.khan.tickets.demo.model.TicketStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class TicketStatusConverter implements AttributeConverter<TicketStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TicketStatus status) {
        if(status == null) {
            return null;
        }

        return status.getId();
    }

    @Override
    public TicketStatus convertToEntityAttribute(Integer id) {
        if(id == null) {
            return null;
        }

        return Stream.of(TicketStatus.values()).filter(s -> s.getId() == id).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
