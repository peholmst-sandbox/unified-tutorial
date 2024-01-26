package com.example.application.ui.component;

import com.example.application.domain.model.Website;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.Nullable;

import java.util.Optional;

public final class WebsiteLink extends Composite<Anchor> {

    private final Span websiteSpan;
    private Website website;

    public WebsiteLink() {
        var icon = VaadinIcon.GLOBE.create();
        icon.addClassNames(LumoUtility.IconSize.SMALL);

        websiteSpan = new Span();

        var helper = new Span("Opens a new window");
        helper.addClassNames(LumoUtility.Accessibility.SCREEN_READER_ONLY);

        getContent().add(icon, websiteSpan, helper);
        getContent().addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL,
                LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        getContent().setTarget("_blank");
    }

    public void setWebsite(@Nullable Website website) {
        this.website = website;
        if (website == null) {
            getContent().setHref("");
            websiteSpan.setText("");
        } else {
            var href = website.toString();
            websiteSpan.setText(trimHref(href));
            getContent().setHref(href);
        }
    }

    private String trimHref(String href) {
        return href.replaceAll("(?:https://|http://)(?:www\\.)?", "");
    }

    public Optional<Website> getWebsite() {
        return Optional.ofNullable(website);
    }
}
