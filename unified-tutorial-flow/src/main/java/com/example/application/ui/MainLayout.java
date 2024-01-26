package com.example.application.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.Optional;

public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private Div viewNavbarContent;

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

        viewTitle = new H1();
        viewTitle.addClassNames(FontSize.LARGE);

        viewNavbarContent = new Div();
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

        addToDrawer(appName, scroller, createFooter());
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
