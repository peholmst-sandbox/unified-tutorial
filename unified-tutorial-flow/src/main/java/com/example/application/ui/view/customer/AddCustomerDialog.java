package com.example.application.ui.view.customer;

import com.example.application.domain.model.Customer;
import com.example.application.service.CustomerService;
import com.example.application.service.IndustryService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.function.SerializableConsumer;

final class AddCustomerDialog extends Dialog {

    private final CustomerEditor editor;
    private final SerializableConsumer<Customer> onSaveCallback;
    private final Button save;

    AddCustomerDialog(CustomerService customerService,
                      IndustryService industryService,
                      SerializableConsumer<Customer> onSaveCallback) {
        editor = new CustomerEditor(customerService, industryService);
        this.onSaveCallback = onSaveCallback;

        save = new Button("Save", event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setDisableOnClick(true);

        var cancel = new Button("Cancel", event -> close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        setWidth("400px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setResizable(true);
        setHeaderTitle("Add Customer");
        add(editor);
        getFooter().add(cancel, save);

        editor.focus();
    }

    private void save() {
        try {
            var savedCustomer = editor.save();
            onSaveCallback.accept(savedCustomer);
            close();
        } catch (ValidationException ex) {
            // Keep the dialog open, the validation errors are already visible
        } finally {
            save.setEnabled(true); // Because of disableOnClick
        }
    }
}
