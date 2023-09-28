package com.khan.tickets.demo.model.converters;

import com.khan.tickets.demo.model.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole userRole) {
        if(userRole == null) {
            return null;
        }

        return userRole.getId();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer id) {
        if(id == null) {
            return null;
        }

        return Stream.of(UserRole.values()).filter(s -> s.getId() == id).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
