package com.example.application.ui.view.customer;

import com.example.application.domain.model.Customer;
import com.example.application.domain.model.Industry;
import com.example.application.service.CustomerService;
import com.example.application.service.IndustryService;
import com.example.application.ui.HasNavbarContent;
import com.example.application.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@PageTitle("Customers")
@Route(value = "customer/:customerId?/:action?(edit)", layout = MainLayout.class)
class CustomerView extends Main implements BeforeEnterObserver, BeforeLeaveObserver, HasNavbarContent {

    private static final String CUSTOMER_ID_ROUTE_PARAM = "customerId";
    private static final String ACTION_ROUTE_PARAM = "action";
    private static final String EDIT_ACTION = "edit";
    static final String SHOW_CUSTOMER_ROUTE_TEMPLATE = "customer/%s";
    static final String EDIT_CUSTOMER_ROUTE_TEMPLATE = "customer/%s/edit";

    private final CustomerService customerService;
    private final IndustryService industryService;
    private final Grid<Customer> grid;
    private final Sidebar sidebar;
    private final Button add;

    // TODO Fluent keyboard navigation support is missing

    public CustomerView(CustomerService customerService,
                        IndustryService industryService) {
        this.customerService = customerService;
        this.industryService = industryService;

        grid = new Grid<>(Customer.class, false);
        sidebar = new Sidebar();
        add = new Button("Add customer", LumoIcon.PLUS.create(), event -> add());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addClassNames(Display.FLEX, Height.FULL);

        add(grid);
        add(sidebar);

        configureGrid();
    }

    private void add() {
        var dialog = new AddCustomerDialog(customerService, industryService, customer -> {
            grid.getDataProvider().refreshAll();
            CustomerNavigation.showCustomerDetails(customer);
        });
        dialog.open();
    }

    @Override
    public Optional<Component> getNavbarContent() {
        return Optional.of(add);
    }

    private void configureGrid() {
        grid.setHeightFull();
        grid.addColumn(Customer.PROP_NAME).setAutoWidth(true);
        grid.addColumn(Customer.PROP_COUNTRY).setAutoWidth(true).setSortable(true); // TODO Can we show a flag?
        grid.addColumn(Customer.PROP_FIRST_CONTACT).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, customer) -> {
                    customer.getIndustries().stream().map(this::createIndustryBadge).forEach(layout::add);
                }))
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Industries");
        grid.addColumn(new ComponentRenderer<>(Anchor::new, (link, customer) -> {
                    Icon icon = VaadinIcon.INFO_CIRCLE.create();
                    icon.addClassName(IconSize.SMALL);

                    link.add(icon); // TODO Actually, this is not editing but showing the details. Another icon?
                    link.addClassNames(AlignItems.CENTER, BoxSizing.BORDER, Display.FLEX, Height.MEDIUM,
                            JustifyContent.CENTER, Width.MEDIUM);
                    link.setHref(SHOW_CUSTOMER_ROUTE_TEMPLATE.formatted(customer.getId()));
                    link.setAriaLabel("Show details of " + customer.getName());
                    link.setTitle("Show details of " + customer.getName());
                }))
                .setAutoWidth(true)
                .setFrozenToEnd(true)
                .setFlexGrow(0);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItems(query -> customerService.list(
                PageRequest.of(
                        query.getPage(),
                        query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        grid.setSelectionMode(Grid.SelectionMode.NONE); // TODO Is this the right way to go?

        var contextMenu = new GridContextMenu<>(grid);

        var titleItem = new H2();
        titleItem.addClassNames(FontSize.MEDIUM, Padding.LARGE);
        contextMenu.add(titleItem);

        contextMenu.addItem("Show Details", e -> e.getItem().ifPresent(CustomerNavigation::showCustomerDetails));
        contextMenu.addItem("Edit Details", e -> e.getItem().ifPresent(CustomerNavigation::editCustomerDetails));
        contextMenu.setDynamicContentHandler(customer -> {
            if (customer == null) {
                return false;
            }
            titleItem.setText(customer.getName());
            return true;
        });
    }

    private Component createIndustryBadge(Industry industry) {
        var span = new Span(industry.getName());
        span.getElement().getThemeList().add("badge");
        return span;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var action = event.getRouteParameters().get(ACTION_ROUTE_PARAM).orElse("");
        var customerId = event.getRouteParameters().get(CUSTOMER_ID_ROUTE_PARAM).map(Long::parseLong).orElse(-1L);

        if (customerId > 0) {
            customerService.get(customerId).ifPresentOrElse(
                    customer -> {
                        sidebar.setCustomer(customer);
                        sidebar.setEditMode(action.equals(EDIT_ACTION));
                        sidebar.setVisible(true);
                    },
                    () -> {
                        Notification.show("Customer not found");
                        grid.getDataProvider().refreshAll();
                        CustomerNavigation.showAllCustomers();
                    });
        } else {
            sidebar.setVisible(false);
            sidebar.setCustomer(null);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (sidebar.isDirty()) {
            var continuationAction = event.postpone();
            var confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Discard changes to '%s'?".formatted(sidebar.getCustomerName()));
            confirmDialog.setText("You have unsaved changes. Are you sure you want to leave?");
            confirmDialog.setConfirmButton("Discard", e -> continuationAction.proceed());
            confirmDialog.setCancelButton("Cancel", e -> continuationAction.cancel());
            confirmDialog.open();
        }
    }

    class Sidebar extends Section {
        private final CustomerEditor editor;
        private final Button edit;
        private final Button save;
        private final Button discard;
        private final Button delete;
        private final Button close;
        private final H2 title;

        Sidebar() {
            editor = new CustomerEditor(customerService, industryService);
            edit = new Button("Edit", LumoIcon.EDIT.create(), event -> edit());
            save = new Button("Save", event -> save());
            discard = new Button("Discard", event -> discard());
            delete = new Button("Delete", VaadinIcon.TRASH.create(), event -> delete());
            close = new Button(VaadinIcon.CLOSE.create(), event -> close());
            title = new H2();

            addClassNames(Border.LEFT, Display.FLEX, FlexDirection.COLUMN);
            setWidth(25, Unit.REM);

            var sidebarContentLayout = new FlexLayout();
            sidebarContentLayout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
            sidebarContentLayout.addClassNames(Flex.GROW, Padding.LARGE);
            add(sidebarContentLayout);

            sidebarContentLayout.add(createHeaderLayout());
            sidebarContentLayout.add(editor);
            add(createButtonLayout());

            setVisible(false);
        }

        private FlexLayout createHeaderLayout() {
            var layout = new FlexLayout();
            layout.setFlexDirection(FlexLayout.FlexDirection.ROW);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            close.setAriaLabel("Close sidebar");
            close.setTooltipText("Close sidebar");

            layout.add(title, close);
            return layout;
        }

        private FlexLayout createButtonLayout() {
            var layout = new FlexLayout();
            layout.setFlexDirection(FlexLayout.FlexDirection.ROW);
            layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
            layout.addClassName(Background.CONTRAST_5);
            layout.addClassName(Padding.Vertical.SMALL);
            layout.addClassName(Padding.Horizontal.MEDIUM);
            layout.addClassName(Gap.MEDIUM);

            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            save.setDisableOnClick(true);
            layout.add(edit, delete, save, discard);
            return layout;
        }

        public void setCustomer(@Nullable Customer customer) {
            editor.populateForm(customer);
        }

        public void setEditMode(boolean editMode) {
            if (editMode) {
                title.setText("Edit details");
            } else {
                title.setText(getCustomerName());
            }
            editor.setReadOnly(!editMode);
            edit.setVisible(!editMode);
            delete.setVisible(!editMode);
            save.setVisible(editMode);
            discard.setVisible(editMode);
        }

        public boolean isDirty() {
            return editor.isDirty();
        }

        private void edit() {
            editor.getCustomer().ifPresent(CustomerNavigation::editCustomerDetails);
        }

        private void save() {
            try {
                var saved = editor.save();
                grid.getDataProvider().refreshItem(saved);
                CustomerNavigation.showCustomerDetails(saved);
            } catch (ValidationException ex) {
                // Keep the dialog open, the validation errors are already visible
            } finally {
                save.setEnabled(true); // Because of disableOnClick
            }
        }

        private void discard() {
            editor.discard(); // To prevent the confirmation dialog from showing up
            editor.getCustomer().ifPresent(CustomerNavigation::showCustomerDetails);
        }

        private void delete() {
            var confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Delete '%s'?".formatted(getCustomerName()));
            confirmDialog.setText("Are you sure you want to permanently delete this customer?");
            confirmDialog.setConfirmButton("Delete", event -> editor.getCustomer().ifPresent(customer -> {
                customerService.delete(customer);
                grid.getDataProvider().refreshAll();
                CustomerNavigation.showAllCustomers();
            }));
            confirmDialog.setConfirmButtonTheme("error primary"); // TODO Is there a utility class for this so that I don't have to use a magic string?
            confirmDialog.setCancelable(true);
            confirmDialog.open();
        }

        private void close() {
            CustomerNavigation.showAllCustomers();
        }

        public String getCustomerName() {
            return editor.getCustomer().map(Customer::getName).orElse("N/A");
        }
    }
}
