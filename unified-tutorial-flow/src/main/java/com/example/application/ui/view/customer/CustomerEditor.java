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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.Nullable;

import java.util.Optional;

class CustomerEditor extends Composite<FormLayout> {
    private final CustomerService customerService;
    private final TextField name = new TextField("Name");
    private final TextField website = new TextField("Website");
    private final Anchor websiteLink = new Anchor();
    private final Span websiteSpan = new Span();
    private final ComboBox<Country> country = new ComboBox<>("Country");
    private final DatePicker firstContact = new DatePicker("First Contact");
    private final MultiSelectComboBox<Industry> industries = new MultiSelectComboBox<>("Industries");
    private final BeanValidationBinder<Customer> binder;
    private Customer customer;
    private boolean dirty = false;
    private boolean readOnly = false;

    CustomerEditor(CustomerService customerService, IndustryService industryService) {
        this.customerService = customerService;

        // Website icon
        Icon icon = VaadinIcon.GLOBE.create();
        icon.addClassName(IconSize.SMALL);

        // Accessibility guidelines
        Span helper = new Span("Opens a new window");
        helper.addClassNames(Accessibility.SCREEN_READER_ONLY);

        websiteLink.add(icon, websiteSpan, helper);
        websiteLink.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL, Margin.Bottom.XSMALL, Margin.Top.MEDIUM);
        websiteLink.setTarget("_blank");
        updateVisitWebsiteVisibility();

        country.setItems(Country.allCountries());
        country.setItemLabelGenerator(Country::getDisplayName);

        industries.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);
        industries.setItemLabelGenerator(Industry::getName);
        industries.setItems(industryService.list());

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
    }

    @Override
    protected FormLayout initContent() {
        var formLayout = new FormLayout();
        formLayout.add(name, website, websiteLink, country, firstContact, industries);
        return formLayout;
    }

    public void populateForm(@Nullable Customer customer) {
        this.customer = customer;
        binder.readBean(customer);
        if (customer != null && customer.getWebsite() != null) {
            String href = customer.getWebsite().toString();
            websiteSpan.setText(href.replace("https://www.", ""));
            websiteLink.setHref(href);
        } else {
            websiteLink.setHref("");
        }
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
        populateForm(customer);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        binder.setReadOnly(readOnly);
        name.setVisible(!readOnly);
        website.setVisible(!readOnly);
        updateVisitWebsiteVisibility();
    }

    private void updateVisitWebsiteVisibility() {
        websiteLink.setVisible(readOnly && !websiteLink.getHref().isEmpty());
    }

    public void focus() {
        name.focus();
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
