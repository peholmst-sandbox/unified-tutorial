package com.example.application.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CountryService {

    private final Map<String, Locale> countries;
    private final List<String> sortedListOfCountryCodes;

    CountryService() {
        countries = Stream.of(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode))
                .collect(Collectors.toMap(Locale::getISO3Country, l -> l));
        sortedListOfCountryCodes = countries
                .values()
                .stream()
                .sorted(Comparator.comparing(l -> l.getDisplayCountry(Locale.ENGLISH)))
                .map(Locale::getISO3Country)
                .collect(Collectors.toList());
    }

    public List<String> countryCodes() {
        return sortedListOfCountryCodes;
    }

    public String countryDisplayName(String countryCode) {
        return Optional.ofNullable(countries.get(countryCode))
                .map(l -> l.getDisplayCountry(Locale.ENGLISH))
                .orElse(countryCode);
    }
}
