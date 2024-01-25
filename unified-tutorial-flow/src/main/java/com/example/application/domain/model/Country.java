package com.example.application.domain.model;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public final class Country {

    private static final List<Country> ALL_COUNTRIES = Stream.of(Locale.getISOCountries())
            .map(Country::new)
            .sorted(Comparator.comparing(Country::getDisplayName))
            .toList();

    public static List<Country> allCountries() {
        return ALL_COUNTRIES;
    }

    private final String isoCode;
    private final String displayName;

    public Country(String isoCode) {
        var locale = new Locale("", isoCode);
        this.isoCode = locale.getCountry();
        this.displayName = locale.getDisplayName(Locale.ENGLISH);
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(isoCode, country.isoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isoCode);
    }
}
