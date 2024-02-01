package com.example.application.ui.views.room;

import com.example.application.service.ChatMessage;
import com.example.application.service.ChatService;
import com.example.application.service.NoSuchChatRoomException;
import com.example.application.ui.CurrentUser;
import com.example.application.ui.MainLayout;
import com.example.application.ui.views.lobby.LobbyView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Route(value = "chatroom", layout = MainLayout.class)
@PermitAll
public class RoomView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private static final int HISTORY_SIZE = 20; // A small number to demonstrate the feature
    private final ChatService chatService;
    private final CurrentUser currentUser;
    private final Div messagesDiv;
    private final TextField messageField;
    private final Button sendMessageButton;
    private String roomName;
    private long roomId;

    public RoomView(ChatService chatService, CurrentUser currentUser) {
        this.chatService = chatService;
        this.currentUser = currentUser;
        setSizeFull();

        messagesDiv = new Div();
        messagesDiv.setSizeFull();
        messagesDiv.addClassNames(Overflow.AUTO, Border.ALL);
        add(messagesDiv);

        messageField = new TextField();
        sendMessageButton = new Button("Send", event -> sendMessage());
        sendMessageButton.setDisableOnClick(true);
        sendMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendMessageButton.addClickShortcut(Key.ENTER);
        Button leaveButton = new Button("Leave", event -> UI.getCurrent().navigate(LobbyView.class));
        leaveButton.addClickShortcut(Key.ESCAPE);

        var toolbar = new HorizontalLayout(messageField, sendMessageButton, leaveButton);
        toolbar.setWidthFull();
        toolbar.expand(messageField);
        add(toolbar);
    }

    private void sendMessage() {
        try {
            var message = messageField.getValue();
            if (!message.isBlank()) {
                chatService.postMessage(roomId, message);
            }
            messageField.clear();
            messageField.focus();
        } finally {
            sendMessageButton.setEnabled(true);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        loadHistory();
        var subscription = chatService.liveMessages(roomId).subscribe(this::onNewMessage);
        addDetachListener(event -> subscription.dispose());
    }

    private void loadHistory() {
        chatService.messageHistory(roomId, HISTORY_SIZE).stream().map(MessageComponent::new).forEach(messagesDiv::add);
        if (messagesDiv.getComponentCount() > 0) {
            messagesDiv.getComponentAt(messagesDiv.getComponentCount() - 1).scrollIntoView();
        }
    }

    private void onNewMessage(ChatMessage chatMessage) {
        getUI().ifPresent(ui -> ui.access(() -> {
            var messageComponent = new MessageComponent(chatMessage);
            messagesDiv.add(messageComponent);

            if (messagesDiv.getComponentCount() > HISTORY_SIZE) {
                messagesDiv.getComponentAt(0).removeFromParent();
            }

            messageComponent.scrollIntoView();

            if (chatMessage.author().equals(currentUser.getName())) {
                messageComponent.addClassNames(Background.CONTRAST_10);
            } else {
                Notification.show("New message from %s".formatted(chatMessage.author()));
            }
        }));
    }

    @Override
    public void setParameter(BeforeEvent event, Long roomId) {
        this.roomId = roomId;
        try {
            roomName = chatService.roomName(roomId);
        } catch (NoSuchChatRoomException ex) {
            event.forwardTo(LobbyView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return roomName;
    }
}
