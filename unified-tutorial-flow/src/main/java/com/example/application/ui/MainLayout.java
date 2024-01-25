package com.example.application.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private Div viewNavbarContent;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        viewNavbarContent = new Div();
        viewNavbarContent.addClassNames(LumoUtility.Margin.Left.LARGE);

        addToNavbar(true, toggle, viewTitle, viewNavbarContent);
    }

    private void addDrawerContent() {
        var appName = new H1("My App");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        var header = new Header(appName);

        var scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        /*nav.addItem(new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));
        nav.addItem(new SideNavItem("Master-Detail", MasterDetailView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));*/

        return nav;
    }

    private Footer createFooter() {
        return new Footer();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
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
