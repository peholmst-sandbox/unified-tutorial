package com.example.application.ui.view.customer;

import com.example.application.domain.model.Country;
import com.example.application.domain.model.Customer;
import com.example.application.domain.model.Industry;
import com.example.application.service.CustomerService;
import com.example.application.service.IndustryService;
import com.example.application.ui.component.WebsiteLink;
import com.example.application.ui.converter.WebsiteConverter;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import jakarta.annotation.Nullable;

import java.util.Optional;

class CustomerEditor extends Composite<FormLayout> {
    private final CustomerService customerService;
    private final TextField name;
    private final TextField website;
    private final WebsiteLink websiteLink;
    private final BeanValidationBinder<Customer> binder;
    private Customer customer;
    private boolean dirty = false;
    private boolean readOnly = false;

    CustomerEditor(CustomerService customerService, IndustryService industryService) {
        this.customerService = customerService;

        name = new TextField("Name");
        website = new TextField("Website");
        websiteLink = new WebsiteLink();

        var country = new ComboBox<Country>("Country");
        country.setItems(Country.allCountries());
        country.setItemLabelGenerator(Country::getDisplayName);

        var firstContact = new DatePicker("First Contact");

        var industries = new MultiSelectComboBox<>("Industries", industryService.list());
        industries.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);
        industries.setItemLabelGenerator(Industry::getName);

        getContent().add(name, website, websiteLink, country, firstContact, industries);

        binder = new BeanValidationBinder<>(Customer.class);
        binder.addValueChangeListener(event -> dirty = true);
        binder.bind(name, Customer.PROP_NAME);
        binder.forField(website)
                .withNullRepresentation("")
                .withConverter(new WebsiteConverter())
                .bind(Customer.PROP_WEBSITE);
        binder.bind(country, Customer.PROP_COUNTRY);
        binder.bind(firstContact, Customer.PROP_FIRST_CONTACT);
        binder.bind(industries, Customer.PROP_INDUSTRIES);

        updateVisitWebsiteVisibility();
    }

    public void setCustomer(@Nullable Customer customer) {
        this.customer = customer;
        binder.readBean(customer);
        websiteLink.setWebsite(customer == null ? null : customer.getWebsite());
        updateVisitWebsiteVisibility();
        this.dirty = false;
    }

    public Optional<Customer> getCustomer() {
        return Optional.ofNullable(customer);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void discard() {
        setCustomer(customer);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        binder.setReadOnly(readOnly);
        name.setVisible(!readOnly);
        website.setVisible(!readOnly);
        updateVisitWebsiteVisibility();
    }

    private void updateVisitWebsiteVisibility() {
        websiteLink.setVisible(readOnly && websiteLink.getWebsite().isPresent());
    }

    public void focus() {
        name.focus();
    }

    public Customer save() throws ValidationException {
        if (customer == null) {
            customer = new Customer();
        }
        binder.writeBean(customer);
        setCustomer(customerService.save(customer));
        return customer;
    }
}
