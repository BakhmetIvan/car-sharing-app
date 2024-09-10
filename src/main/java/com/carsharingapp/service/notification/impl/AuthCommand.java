package com.carsharingapp.service.notification.impl;

import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.messages.NotificationMessages;
import com.carsharingapp.security.AuthenticationService;
import com.carsharingapp.service.notification.TelegramCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommand implements TelegramCommand {
    private final AuthenticationService authenticationService;

    @Override
    public String execute(String messageText, Long chatId) {
        String[] parts = messageText.split(" ");
        if (parts.length == 3) {
            String email = parts[1];
            String password = parts[2];
            try {
                if (authenticationService.telegramAdminsAuthenticate(email, password, chatId)) {
                    return NotificationMessages.AUTH_SUCCESS_MESSAGE;
                } else {
                    return NotificationMessages.NO_ADMIN_RIGHTS_MESSAGE;
                }
            } catch (BadCredentialsException e) {
                return NotificationMessages.AUTH_FAILURE_MESSAGE;
            } catch (AuthenticationException e) {
                return NotificationMessages.INVALID_PASSWORD_MESSAGE;
            }
        } else {
            return ExceptionMessages.INVALID_COMMAND_FORMAT_MSG;
        }
    }
}
