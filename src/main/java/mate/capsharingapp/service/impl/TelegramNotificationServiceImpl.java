package mate.capsharingapp.service.impl;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mate.capsharingapp.messages.ExceptionMessages;
import mate.capsharingapp.messages.NotificationMessages;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.UserRepository;
import mate.capsharingapp.security.AuthenticationService;
import mate.capsharingapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Setter
@Getter
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl extends TelegramLongPollingBot
        implements NotificationService {
    private static final String AUTH_COMMAND = "/auth ";
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (messageText.startsWith(AUTH_COMMAND)) {
            handleAuthCommand(messageText, chatId);
        } else {
            sendNotification(chatId, ExceptionMessages.INVALID_COMMAND_FORMAT_MSG);
        }
    }

    @Override
    public void sendNotificationToAdmins(String message) {
        List<User> admins = userRepository.findAllByRolesName(Role.RoleName.ROLE_MANAGER);
        for (User user : admins) {
            if (user.getTgChatId() != null) {
                sendNotification(user.getTgChatId(), message);
            }
        }
    }

    public void sendNotification(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleAuthCommand(String messageText, Long chatId) {
        String[] parts = messageText.split(" ");
        if (parts.length == 3) {
            String email = parts[1];
            String password = parts[2];
            try {
                if (authenticationService.telegramAdminsAuthenticate(email, password, chatId)) {
                    sendNotification(chatId, NotificationMessages.AUTH_SUCCESS_MESSAGE);
                } else {
                    sendNotification(chatId, NotificationMessages.NO_ADMIN_RIGHTS_MESSAGE);
                }
            } catch (BadCredentialsException e) {
                sendNotification(chatId, NotificationMessages.AUTH_FAILURE_MESSAGE);
            } catch (AuthenticationException e) {
                sendNotification(chatId, NotificationMessages.INVALID_PASSWORD_MESSAGE);
            }
        } else {
            sendNotification(chatId, ExceptionMessages.INVALID_COMMAND_FORMAT_MSG);
        }
    }
}
