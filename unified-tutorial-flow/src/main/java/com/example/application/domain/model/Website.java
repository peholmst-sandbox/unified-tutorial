package com.example.application.domain.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;

public final class Website {

    private final String website;

    public Website(String website) {
        try {
            var url = URI.create(website).toURL();
            if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
                throw new IllegalArgumentException("Invalid website URL");
            }
            if (website.length() > 255) { // Standard column length in JPA
                throw new IllegalArgumentException("Website URL is too long");
            }
            this.website = url.toString();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid website URL");
        }
    }

    @Override
    public String toString() {
        return website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website1 = (Website) o;
        return Objects.equals(website, website1.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(website);
    }
}
