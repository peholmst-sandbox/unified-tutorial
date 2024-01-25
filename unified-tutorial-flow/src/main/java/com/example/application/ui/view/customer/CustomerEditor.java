package com.example.application.ui.view.customer;

import com.example.application.domain.model.Country;
import com.example.application.domain.model.Customer;
import com.example.application.domain.model.Industry;
import com.example.application.service.CustomerService;
import com.example.application.service.IndustryService;
import com.example.application.ui.converter.WebsiteConverter;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.Nullable;

import java.util.Optional;

class CustomerEditor extends Composite<FormLayout> {
    private final IndustryService industryService;
    private final CustomerService customerService;
    private final TextField name = new TextField("Name");
    private final TextField website = new TextField("Website");
    private final Anchor websiteLink = new Anchor();
    private final ComboBox<Country> country = new ComboBox<>("Country");
    private final DatePicker firstContact = new DatePicker("First Contact");
    private final MultiSelectComboBox<Industry> industries = new MultiSelectComboBox<>("Industries");
    private final BeanValidationBinder<Customer> binder;
    private Customer customer;

    CustomerEditor(CustomerService customerService, IndustryService industryService) {
        this.customerService = customerService;
        this.industryService = industryService;

        binder = new BeanValidationBinder<>(Customer.class);
        binder.bind(name, Customer.PROP_NAME);
        binder.forField(website)
                .withNullRepresentation("")
                .withConverter(new WebsiteConverter())
                .bind(Customer.PROP_WEBSITE);
        binder.bind(country, Customer.PROP_COUNTRY);
        binder.bind(firstContact, Customer.PROP_FIRST_CONTACT);
        binder.bind(industries, Customer.PROP_INDUSTRIES);
    }

    @Override
    protected FormLayout initContent() {
        var formLayout = new FormLayout();
        var websiteLinkIcon = VaadinIcon.EXTERNAL_LINK.create();
        websiteLinkIcon.addClassName(LumoUtility.IconSize.SMALL);
        websiteLink.add(websiteLinkIcon);
        websiteLink.setAriaLabel("Visit website");
        websiteLink.setTarget("_blank");
        website.setSuffixComponent(websiteLink);

        country.setItems(Country.allCountries());
        country.setItemLabelGenerator(Country::getDisplayName);

        industries.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);
        industries.setItemLabelGenerator(Industry::getName);
        industries.setItems(industryService.list());

        formLayout.add(name, website, country, firstContact, industries);
        return formLayout;
    }

    public void populateForm(@Nullable Customer customer) {
        this.customer = customer;
        binder.readBean(customer);
        websiteLink.setVisible(isWebsiteLinkVisible());
        if (customer != null && customer.getWebsite() != null) {
            websiteLink.setHref(customer.getWebsite().toString());
        }
    }

    private boolean isWebsiteLinkVisible() {
        return customer != null && customer.getWebsite() != null && website.isReadOnly();
    }

    public Optional<Customer> getCustomer() {
        return Optional.ofNullable(customer);
    }

    public void clearForm() {
        populateForm(null);
    }

    public void discardChanges() {
        binder.readBean(customer);
    }

    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
        websiteLink.setVisible(isWebsiteLinkVisible());
    }

    public void focus() {
        name.focus();
    }

    public void setNameVisible(boolean visible) {
        name.setVisible(visible);
    }

    public Customer save() throws ValidationException {
        if (customer == null) {
            customer = new Customer();
        }
        binder.writeBean(customer);
        populateForm(customerService.save(customer));
        return customer;
    }
}
