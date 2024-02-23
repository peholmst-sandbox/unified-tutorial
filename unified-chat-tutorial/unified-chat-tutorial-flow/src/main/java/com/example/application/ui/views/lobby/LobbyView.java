package com.example.application.ui.views.lobby;

import com.example.application.chat.Channel;
import com.example.application.chat.ChatService;
import com.example.application.security.Roles;
import com.example.application.ui.MainLayout;
import com.example.application.ui.views.channel.ChannelView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Lobby")
@PermitAll
public class LobbyView extends VerticalLayout {

    private final ChatService chatService;
    private final VirtualList<Channel> channels;
    private final TextField channelNameField;
    private final Button addChannelButton;

    public LobbyView(ChatService chatService, AuthenticationContext authenticationContext) {
        this.chatService = chatService;
        setSizeFull();
        addClassName("lobby-view");

        channels = new VirtualList<>();
        channels.addClassNames(Border.ALL, Padding.SMALL);
        channels.setRenderer(new ComponentRenderer<>(this::createChannelComponent));
        add(channels);
        expand(channels);

        channelNameField = new TextField();
        channelNameField.setPlaceholder("New channel name");

        addChannelButton = new Button("Add channel", event -> addChannel());
        addChannelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addChannelButton.addClickShortcut(Key.ENTER);
        addChannelButton.setDisableOnClick(true);

        if (authenticationContext.hasRole(Roles.ADMIN)) {
            var toolbar = new HorizontalLayout(channelNameField,
                    addChannelButton);
            toolbar.setWidthFull();
            toolbar.expand(channelNameField);
            add(toolbar);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refreshChannels();
    }

    private void addChannel() {
        try {
            var nameOfNewChannel = channelNameField.getValue();
            if (!nameOfNewChannel.isBlank()) {
                chatService.createChannel(nameOfNewChannel);
                channelNameField.clear();
                refreshChannels();
            }
        } finally {
            addChannelButton.setEnabled(true);
        }
    }

    private void refreshChannels() {
        channels.setItems(chatService.channels());
    }

    private Component createChannelComponent(Channel channel) {
        var div = new Div();
        div.addClassNames(Display.FLEX, Gap.MEDIUM, Padding.MEDIUM, BorderRadius.MEDIUM, "channel");

        var avatar = new Avatar(channel.name());
        avatar.setColorIndex(Math.abs(channel.id().hashCode() % 7));
        div.add(avatar);

        var contentDiv = new Div();
        contentDiv.addClassNames(Display.FLEX, Flex.AUTO, FlexDirection.COLUMN, LineHeight.XSMALL, Gap.XSMALL);
        div.add(contentDiv);

        var channelDiv = new Div();
        channelDiv.addClassNames(Display.FLEX, AlignItems.BASELINE, JustifyContent.START, Gap.SMALL);
        contentDiv.add(channelDiv);

        var channelLink = new RouterLink(channel.name(), ChannelView.class, channel.id());
        channelLink.addClassNames(FontSize.MEDIUM, FontWeight.BOLD, TextColor.BODY);
        channelDiv.add(channelLink);

        if (channel.lastMessage() != null) {
            var lastMessageTimestamp = new Div(formatInstant(channel.lastMessage().timestamp(), getLocale()));
            lastMessageTimestamp.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
            channelDiv.add(lastMessageTimestamp);
        }

        var lastMessage = new Div();
        lastMessage.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        contentDiv.add(lastMessage);
        if (channel.lastMessage() != null) {
            var author = new Span(channel.lastMessage().author());
            author.addClassNames(FontWeight.BOLD);
            lastMessage.add(author, new Text(": " + truncateMessage(channel.lastMessage().message())));
        } else {
            lastMessage.setText("No messages yet");
        }
        return div;
    }

    private String truncateMessage(String msg) {
        return msg.length() > 50 ? msg.substring(0, 50) + "..." : msg;
    }

    private String formatInstant(Instant instant, Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(locale)
                .format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }
}
