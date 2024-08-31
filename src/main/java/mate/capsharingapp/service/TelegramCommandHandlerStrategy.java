package mate.capsharingapp.service;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.service.impl.AuthCommand;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramCommandHandlerStrategy {
    private static final String AUTH_COMMAND = "/auth";
    private final AuthCommand authCommand;

    public TelegramCommand handleCommand(String messageText) {
        String commandKey = messageText.split(" ")[0];
        switch (commandKey) {
            case AUTH_COMMAND:
                return authCommand;
            default:
                return null;
        }
    }
}
