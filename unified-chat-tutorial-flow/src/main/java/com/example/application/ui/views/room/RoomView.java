package com.example.application.ui.views.room;

import com.example.application.service.ChatMessage;
import com.example.application.service.ChatService;
import com.example.application.service.NoSuchChatRoomException;
import com.example.application.ui.CurrentUser;
import com.example.application.ui.MainLayout;
import com.example.application.ui.views.lobby.LobbyView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.LinkedList;
import java.util.List;

import static com.vaadin.flow.theme.lumo.LumoUtility.Background;
import static com.vaadin.flow.theme.lumo.LumoUtility.Border;

@Route(value = "chatroom", layout = MainLayout.class)
@PermitAll
public class RoomView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private static final int HISTORY_SIZE = 20; // A small number to demonstrate the feature
    private final ChatService chatService;
    private final CurrentUser currentUser;
    private final MessageList messageList;
    private final List<MessageListItem> messageListItemList;
    private String roomName;
    private long roomId;

    public RoomView(ChatService chatService, CurrentUser currentUser) {
        this.chatService = chatService;
        this.currentUser = currentUser;
        setSizeFull();

        messageListItemList = new LinkedList<>();

        messageList = new MessageList();
        messageList.addClassNames(Border.ALL);
        messageList.setSizeFull();
        add(messageList);

        var messageInput = new MessageInput(event -> sendMessage(event.getValue()));
        messageInput.setWidthFull();

        add(messageInput);
    }

    private void sendMessage(String message) {
        if (!message.isBlank()) {
            chatService.postMessage(roomId, message);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        loadHistory();
        var subscription = chatService.liveMessages(roomId).subscribe(this::onNewMessage);
        addDetachListener(event -> subscription.dispose());
    }

    private void loadHistory() {
        messageListItemList.clear();
        chatService.messageHistory(roomId, HISTORY_SIZE).stream().map(this::createMessageListItem).forEach(messageListItemList::add);
        messageList.setItems(messageListItemList);
    }

    private void onNewMessage(ChatMessage chatMessage) {
        getUI().ifPresent(ui -> ui.access(() -> {
            var messageListItem = createMessageListItem(chatMessage);
            messageListItemList.add(messageListItem);

            if (messageListItemList.size() > HISTORY_SIZE) {
                messageListItemList.removeFirst();
            }

            if (!chatMessage.author().equals(currentUser.getName())) {
                Notification.show("New message from %s".formatted(chatMessage.author()));
            }

            messageList.setItems(messageListItemList);
        }));
    }

    private MessageListItem createMessageListItem(ChatMessage chatMessage) {
        var item = new MessageListItem(chatMessage.message(), chatMessage.timestamp(), chatMessage.author());
        if (chatMessage.author().equals(currentUser.getName())) {
            item.addClassNames(Background.CONTRAST_10);
        }
        return item;
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
