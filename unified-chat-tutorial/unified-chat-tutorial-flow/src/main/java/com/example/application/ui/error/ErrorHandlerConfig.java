package com.example.application.ui.error;

import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ErrorHandlerConfig {

    @Bean
    public VaadinServiceInitListener vaadinServiceInitListener() {
        return event -> event.getSource().addSessionInitListener(e -> e.getSession().setErrorHandler(new ErrorHandler()));
    }
}
