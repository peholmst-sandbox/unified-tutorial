package com.example.application.domain.model.jpa;

import com.example.application.domain.model.Country;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CountryAttributeConverter implements AttributeConverter<Country, String> {

    @Override
    public String convertToDatabaseColumn(Country attribute) {
        return attribute == null ? null : attribute.getIsoCode();
    }

    @Override
    public Country convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Country(dbData);
    }
}
