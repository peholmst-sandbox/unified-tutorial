package com.example.application.ui.views.room;

import com.example.application.service.ChatMessage;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;

class MessageComponent extends Div {

    public MessageComponent(ChatMessage chatMessage) {
        var avatar = new Avatar(chatMessage.author());

        var author = new Div(chatMessage.author());
        author.addClassNames(LumoUtility.FontWeight.MEDIUM, LumoUtility.FontSize.MEDIUM);

        var timestamp = new Div(chatMessage.timestamp().toString());
        timestamp.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        var header = new Div(author, timestamp);
        header.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.AlignItems.CENTER,
                LumoUtility.Gap.MEDIUM);

        var message = new Div(chatMessage.message());

        var messageWrapper = new Div(header, message);
        messageWrapper.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.SMALL);

        add(avatar, messageWrapper);
        addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.BoxShadow.SMALL, LumoUtility.BorderRadius.SMALL,
                LumoUtility.Margin.MEDIUM, LumoUtility.Padding.SMALL, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.ROW);

    }
}
