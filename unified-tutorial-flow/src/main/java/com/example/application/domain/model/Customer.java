package com.example.application.domain.model;

import com.example.application.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.util.Set;

@Entity
public class Customer extends BaseEntity {

    public static final String PROP_NAME = "name";
    public static final String PROP_WEBSITE = "website";
    public static final String PROP_COUNTRY = "country";
    public static final String PROP_INDUSTRIES = "industries";

    private String name;
    private String website;
    private String country;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Industry> industries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Industry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<Industry> industries) {
        this.industries = industries;
    }
}
