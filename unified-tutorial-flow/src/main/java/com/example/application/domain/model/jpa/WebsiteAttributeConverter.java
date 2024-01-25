package com.example.application.domain.model.jpa;

import com.example.application.domain.model.Website;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class WebsiteAttributeConverter implements AttributeConverter<Website, String> {

    @Override
    public String convertToDatabaseColumn(Website attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public Website convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Website(dbData);
    }
}
