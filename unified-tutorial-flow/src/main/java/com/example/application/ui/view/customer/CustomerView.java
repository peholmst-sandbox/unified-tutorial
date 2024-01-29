package com.example.application.ui.view.customer;

import com.example.application.domain.model.Customer;
import com.example.application.domain.model.Industry;
import com.example.application.service.CustomerService;
import com.example.application.service.IndustryService;
import com.example.application.ui.HasNavbarContent;
import com.example.application.ui.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@PageTitle("Customers")
@Route(value = "customer/:customerId?/:action?(edit)", layout = MainLayout.class)
public final class CustomerView extends Main implements BeforeEnterObserver, BeforeLeaveObserver, HasNavbarContent {

    private static final String CUSTOMER_ID_ROUTE_PARAM = "customerId";
    private static final String ACTION_ROUTE_PARAM = "action";
    private static final String EDIT_ACTION = "edit";
    public static final String SIDEBAR_TITLE_ID = "sidebar-title";

    static RouteParameters createShowCustomerRouteParameters(Customer customer) {
        return new RouteParameters(CUSTOMER_ID_ROUTE_PARAM, customer.getId().toString());
    }

    static RouteParameters createEditCustomerRouteParameters(Customer customer) {
        return new RouteParameters(Map.of(
                CustomerView.CUSTOMER_ID_ROUTE_PARAM, customer.getId().toString(),
                CustomerView.ACTION_ROUTE_PARAM, CustomerView.EDIT_ACTION
        ));
    }

    public static void editCustomerDetails(Customer customer) {
        if (customer.getId() == null) {
            showAllCustomers();
        } else {
            UI.getCurrent().navigate(CustomerView.class, createEditCustomerRouteParameters(customer));
        }
    }

    public static void showCustomerDetails(Customer customer) {
        if (customer.getId() == null) {
            showAllCustomers();
        } else {
            UI.getCurrent().navigate(CustomerView.class, createShowCustomerRouteParameters(customer));
        }
    }

    public static void showAllCustomers() {
        UI.getCurrent().navigate(CustomerView.class);
    }

    private final CustomerService customerService;
    private final IndustryService industryService;
    private final Grid<Customer> grid;
    private final Sidebar sidebar;
    private final Button add;

    CustomerView(CustomerService customerService,
                 IndustryService industryService) {
        this.customerService = customerService;
        this.industryService = industryService;

        grid = new Grid<>(Customer.class, false);
        sidebar = new Sidebar();
        add = new Button("Add customer", LumoIcon.PLUS.create(), event -> add());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addClassNames(Display.FLEX, Height.FULL, Overflow.HIDDEN, Width.FULL);

        add(grid);
        add(sidebar);

        configureGrid();
    }

    private void add() {
        var dialog = new AddCustomerDialog(customerService, industryService, customer -> {
            grid.getDataProvider().refreshAll();
            showCustomerDetails(customer);
        });
        dialog.open();
    }

    @Override
    public Collection<Component> getNavbarContent() {
        return List.of(add);
    }

    private void configureGrid() {
        grid.setHeightFull();
        grid.addColumn(Customer.PROP_NAME).setAutoWidth(true);
        grid.addColumn(Customer.PROP_COUNTRY).setAutoWidth(true).setSortable(true);
        grid.addColumn(Customer.PROP_FIRST_CONTACT).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, customer) -> {
                    customer.getIndustries().stream().map(this::createIndustryBadge).forEach(layout::add);
                }))
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Industries");
        grid.addColumn(new ComponentRenderer<>(RouterLink::new, (link, customer) -> {
                    var icon = VaadinIcon.INFO_CIRCLE.create();
                    icon.addClassName(IconSize.SMALL);

                    link.add(icon);
                    link.addClassNames(AlignItems.CENTER, BoxSizing.BORDER, Display.FLEX, Height.MEDIUM,
                            JustifyContent.CENTER, Width.MEDIUM);
                    link.setRoute(CustomerView.class, createShowCustomerRouteParameters(customer));
                    link.getElement().setAttribute("aria-label", "Show details of " + customer.getName());
                    link.getElement().setAttribute("title", "Show details of " + customer.getName());
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
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        var contextMenu = new GridContextMenu<>(grid);

        var titleItem = new H2();
        titleItem.addClassNames(FontSize.MEDIUM, Padding.LARGE);
        contextMenu.add(titleItem);

        contextMenu.addItem("Show Details", e -> e.getItem().ifPresent(CustomerView::showCustomerDetails));
        contextMenu.addItem("Edit Details", e -> e.getItem().ifPresent(CustomerView::editCustomerDetails));
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
                        sidebar.focus();
                    },
                    () -> {
                        Notification.show("Customer not found");
                        grid.getDataProvider().refreshAll();
                        showAllCustomers();
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

    class Sidebar extends Section implements HasAriaLabel, Focusable<Section> {
        private final CustomerEditor editor;
        private final Button edit;
        private final Button save;
        private final Button discard;
        private final Button delete;
        private final H2 title;

        Sidebar() {
            getStyle().set("transition", "margin-inline-end var(--vaadin-app-layout-transition)");

            editor = new CustomerEditor(customerService, industryService);
            editor.addClassNames(Flex.GROW, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

            edit = new Button("Edit", LumoIcon.EDIT.create(), event -> edit());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            save = new Button("Save", LumoIcon.CHECKMARK.create(), event -> save());
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            save.setDisableOnClick(true);

            discard = new Button("Discard", LumoIcon.UNDO.create(), event -> discard());
            discard.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            delete = new Button("Delete", VaadinIcon.TRASH.create(), event -> delete());
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            var close = new Button(LumoIcon.CROSS.create(), event -> close());
            close.addClassNames(TextColor.SECONDARY);
            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            close.setAriaLabel("Close sidebar");
            close.setTooltipText("Close sidebar");

            title = new H2();
            title.addClassNames(FontSize.XLARGE, LineHeight.SMALL);
            title.setId(SIDEBAR_TITLE_ID);

            addClassNames(BoxShadow.SMALL, Display.FLEX, FlexDirection.COLUMN, Position.RELATIVE);
            setAriaLabelledBy(SIDEBAR_TITLE_ID);
            setTabIndex(0);
            setVisible(false);
            setWidth(25, Unit.REM);

            var header = new Div(title, close);
            header.addClassNames(AlignItems.CENTER, Display.FLEX, Height.XLARGE, JustifyContent.BETWEEN,
                    Padding.Horizontal.LARGE);

            var footer = new Div(edit, delete, save, discard);
            footer.addClassNames(Background.CONTRAST_5, Display.FLEX, Gap.SMALL, Padding.Horizontal.MEDIUM,
                    Padding.Vertical.SMALL);

            add(header, editor, footer);
        }

        public void setCustomer(@Nullable Customer customer) {
            editor.setCustomer(customer);
        }

        public void setEditMode(boolean editMode) {
            if (editMode) {
                title.setText("Edit " + getCustomerName());
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
            editor.getCustomer().ifPresent(CustomerView::editCustomerDetails);
        }

        private void save() {
            try {
                var saved = editor.save();
                grid.getDataProvider().refreshItem(saved);
                showCustomerDetails(saved);
            } catch (ValidationException ex) {
                // Keep the dialog open, the validation errors are already visible
            } finally {
                save.setEnabled(true); // Because of disableOnClick
            }
        }

        private void discard() {
            editor.discard(); // To prevent the confirmation dialog from showing up
            editor.getCustomer().ifPresent(CustomerView::showCustomerDetails);
        }

        private void delete() {
            var confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Delete '%s'?".formatted(getCustomerName()));
            confirmDialog.setText("Are you sure you want to permanently delete this customer?");
            confirmDialog.setConfirmButton("Delete", event -> editor.getCustomer().ifPresent(customer -> {
                customerService.delete(customer);
                grid.getDataProvider().refreshAll();
                showAllCustomers();
            }));
            confirmDialog.setConfirmButtonTheme("error primary");
            confirmDialog.setCancelable(true);
            confirmDialog.open();
        }

        private void close() {
            showAllCustomers();
        }

        public String getCustomerName() {
            return editor.getCustomer().map(Customer::getName).orElse("N/A");
        }

        @Override
        public void focus() {
            Element element = getElement();
            element.executeJs("setTimeout(function(){$0.focus({preventScroll:true})},0)", new Serializable[]{element});
        }

        @Override
        public void setVisible(boolean visible) {
            if (visible) {
                getStyle().set("margin-inline-end", "0");
            } else {
                getStyle().set("margin-inline-end", "-" + getWidth());
            }
        }
    }
}
