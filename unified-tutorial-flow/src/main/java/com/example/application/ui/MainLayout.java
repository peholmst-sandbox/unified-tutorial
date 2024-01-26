package com.example.application.ui;

import com.example.application.ui.view.customer.CustomerNavigation;
import com.example.application.ui.view.start.StartView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.Optional;

public class MainLayout extends AppLayout {

    // TODO Is it a good practice to create the fields like this, and configure them in a private method,
    //  or should they be configured in the constructor?
    private final H1 viewTitle = new H1();
    private final Div viewNavbarContent = new Div();

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.addClassNames(TextColor.SECONDARY);
        toggle.setAriaLabel("Menu toggle");
        toggle.setTooltipText("Menu toggle");

        viewTitle.addClassNames(FontSize.LARGE);

        viewNavbarContent.addClassNames(Margin.Start.AUTO);

        var header = new Header(toggle, viewTitle, viewNavbarContent);
        header.addClassNames(AlignItems.CENTER, Display.FLEX, Padding.End.MEDIUM, Width.FULL);

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        var appName = new Span("My App");
        appName.addClassNames(AlignItems.CENTER, Display.FLEX, FontSize.LARGE, FontWeight.SEMIBOLD, Height.XLARGE,
                Padding.Horizontal.MEDIUM);

        var scroller = new Scroller(createNavigation());

        addToDrawer(appName, scroller);
    }

    private SideNav createNavigation() {
        var nav = new SideNav();
        // TODO Here we refer to the StartView directly and to the CustomerView indirectly. Is this inconsistent?
        nav.addItem(new SideNavItem("Start", StartView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Customers", CustomerNavigation.allCustomersView(), VaadinIcon.BRIEFCASE.create()));
        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        // TODO Is it a good idea to allow views to add components to the navbar?
        viewNavbarContent.removeAll();
        getCurrentNavbarContent().ifPresent(viewNavbarContent::add);
    }

    private String getCurrentPageTitle() {
        var title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Optional<Component> getCurrentNavbarContent() {
        if (getContent() instanceof HasNavbarContent content) {
            return content.getNavbarContent();
        } else {
            return Optional.empty();
        }
    }
}
