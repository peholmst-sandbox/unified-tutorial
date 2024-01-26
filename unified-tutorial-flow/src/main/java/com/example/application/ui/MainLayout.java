package com.example.application.ui;

import com.example.application.ui.view.customer.CustomerView;
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

import java.util.Collection;
import java.util.Collections;

public final class MainLayout extends AppLayout {

    private final H1 viewTitle;
    private final Div viewNavbarContent;

    MainLayout() {
        viewTitle = new H1();
        viewTitle.addClassNames(FontSize.LARGE);

        viewNavbarContent = new Div();
        viewNavbarContent.addClassNames(Margin.Start.AUTO);

        var toggle = new DrawerToggle();
        toggle.addClassNames(TextColor.SECONDARY);
        toggle.setAriaLabel("Menu toggle");
        toggle.setTooltipText("Menu toggle");

        var header = new Header(toggle, viewTitle, viewNavbarContent);
        header.addClassNames(AlignItems.CENTER, Display.FLEX, Padding.End.MEDIUM, Width.FULL);
        addToNavbar(true, header);

        var appName = new Span("Pro CRM Deluxe");
        appName.addClassNames(AlignItems.CENTER, Display.FLEX, FontSize.LARGE, FontWeight.SEMIBOLD, Height.XLARGE,
                Padding.Horizontal.MEDIUM);

        addToDrawer(appName, new Scroller(createSideNav()));
        setPrimarySection(Section.DRAWER);
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addItem(new SideNavItem("Start", StartView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Customers", CustomerView.class, VaadinIcon.BRIEFCASE.create()));
        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        viewNavbarContent.removeAll();
        viewNavbarContent.add(getCurrentNavbarContent());
    }

    private String getCurrentPageTitle() {
        var title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Collection<Component> getCurrentNavbarContent() {
        if (getContent() instanceof HasNavbarContent content) {
            return content.getNavbarContent();
        } else {
            return Collections.emptyList();
        }
    }
}
