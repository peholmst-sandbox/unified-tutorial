package com.example.application.ui.view.customer;

import com.example.application.domain.model.Customer;
import com.vaadin.flow.component.UI;

public final class CustomerNavigation {

    private CustomerNavigation() {
    }

    public static void editCustomerDetails(Customer customer) {
        if (customer.getId() == null) {
            showAllCustomers();
        } else {
            UI.getCurrent().navigate(CustomerView.EDIT_CUSTOMER_ROUTE_TEMPLATE.formatted(customer.getId()));
        }
    }

    public static void showCustomerDetails(Customer customer) {
        if (customer.getId() == null) {
            showAllCustomers();
        } else {
            UI.getCurrent().navigate(CustomerView.SHOW_CUSTOMER_ROUTE_TEMPLATE.formatted(customer.getId()));
        }
    }

    public static void showAllCustomers() {
        UI.getCurrent().navigate(CustomerView.class);
    }
}
