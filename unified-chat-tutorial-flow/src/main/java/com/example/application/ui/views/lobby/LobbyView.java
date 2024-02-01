package com.example.application.ui.views.lobby;

import com.example.application.service.ChatRoom;
import com.example.application.service.ChatService;
import com.example.application.ui.MainLayout;
import com.example.application.ui.views.room.RoomView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Lobby")
public class LobbyView extends VerticalLayout {

    private final ChatService chatService;
    private final Grid<ChatRoom> rooms;
    private final TextField roomNameField;
    private final Button addRoomButton;

    public LobbyView(ChatService chatService) {
        this.chatService = chatService;
        setSizeFull();

        rooms = new Grid<>();
        rooms.setSizeFull();
        rooms.addColumn(new ComponentRenderer<>(RouterLink::new, (link, room) -> {
            link.setText(room.name());
            link.setRoute(RoomView.class, room.id());
        })).setHeader("Room Name");
        rooms.addColumn(room -> {
            var lastMessage = room.lastMessage();
            return lastMessage == null ? "Never" : lastMessage.toString();
        }).setHeader("Last Message");
        add(rooms);

        roomNameField = new TextField();
        roomNameField.setPlaceholder("New chat room name");

        addRoomButton = new Button("Add room", event -> addRoom());
        addRoomButton.addClickShortcut(Key.ENTER);
        addRoomButton.setDisableOnClick(true);

        var toolbar = new HorizontalLayout(roomNameField, addRoomButton);
        toolbar.setWidthFull();
        toolbar.expand(roomNameField);
        add(toolbar);

        refreshRooms();
    }

    private void addRoom() {
        try {
            var nameOfNewRoom = roomNameField.getValue();
            if (!nameOfNewRoom.isBlank()) {
                chatService.createRoom(nameOfNewRoom);
                roomNameField.clear();
                refreshRooms();
            }
        } finally {
            addRoomButton.setEnabled(true);
        }
    }

    private void refreshRooms() {
        rooms.setItems(chatService.rooms());
    }
}
