package com.example.application.ui.error;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

public class ErrorHandler implements com.vaadin.flow.server.ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void error(ErrorEvent event) {
        if (event.getThrowable() instanceof OptimisticLockingFailureException) {
            showError("Somebody else updated the record while you were making changes.");
        } else {
            log.error("Unexpected error caught", event.getThrowable());
            showError("An unexpected error has occurred. Please try again later.");
        }
    }

    private void showError(String error) {
        Optional.ofNullable(UI.getCurrent()).ifPresent(ui -> ui.access(() -> {
            var notification = Notification.show(error);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }));
    }
}
