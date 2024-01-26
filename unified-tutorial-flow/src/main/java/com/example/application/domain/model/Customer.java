package com.example.application.domain.model;

import com.example.application.domain.model.jpa.CountryAttributeConverter;
import com.example.application.domain.model.jpa.WebsiteAttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Entity
public class Customer extends BaseEntity {

    public static final String PROP_NAME = "name";
    public static final String PROP_WEBSITE = "website";
    public static final String PROP_COUNTRY = "country";
    public static final String PROP_FIRST_CONTACT = "firstContact";
    public static final String PROP_INDUSTRIES = "industries";

    @NotEmpty(message = "Please enter a name")
    @Length(max = 255)
    private String name;
    @Convert(converter = WebsiteAttributeConverter.class)
    private Website website;
    @Convert(converter = CountryAttributeConverter.class)
    @NotNull(message = "Please select a country")
    private Country country;
    private LocalDate firstContact;
    @ManyToMany(fetch = FetchType.EAGER)
    @NotEmpty(message = "Please select at least one industry")
    private Set<Industry> industries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Website getWebsite() {
        return website;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public LocalDate getFirstContact() {
        return firstContact;
    }

    public void setFirstContact(LocalDate firstContact) {
        this.firstContact = firstContact;
    }

    public Set<Industry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<Industry> industries) {
        this.industries = industries;
    }
}
