package com.example.application.ui.view.customer;

import com.example.application.domain.model.Customer;
import com.vaadin.flow.component.Component;
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

    public static Class<? extends Component> allCustomersView() {
        // TODO This may be overkill but the intention is to keep the CustomerView class package private while still allowing
        //  it to be added to the navigation menu. Might be better to just make CustomerView public.
        return CustomerView.class;
    }
}
