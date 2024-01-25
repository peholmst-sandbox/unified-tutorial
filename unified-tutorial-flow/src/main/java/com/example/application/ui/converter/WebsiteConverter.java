package com.example.application.ui.converter;

import com.example.application.domain.model.Website;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class WebsiteConverter implements Converter<String, Website> {

    @Override
    public Result<Website> convertToModel(String value, ValueContext context) {
        try {
            return value == null || value.isEmpty() ? Result.ok(null) : Result.ok(new Website(value));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Override
    public String convertToPresentation(Website value, ValueContext context) {
        return value == null ? null : value.toString();
    }
}
