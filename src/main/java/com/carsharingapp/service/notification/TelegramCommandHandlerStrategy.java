package com.carsharingapp.service.notification;

import com.carsharingapp.exception.TelegramException;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.service.notification.impl.AuthCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramCommandHandlerStrategy {
    private static final String AUTH_COMMAND = "/auth";
    private final AuthCommand authCommand;

    public TelegramCommand handleCommand(String messageText) throws TelegramException {
        String commandKey = messageText.split(" ")[0];
        switch (commandKey) {
            case AUTH_COMMAND:
                return authCommand;
            default:
                throw new TelegramException(ExceptionMessages.INVALID_COMMAND_FORMAT_MSG);
        }
    }
}
