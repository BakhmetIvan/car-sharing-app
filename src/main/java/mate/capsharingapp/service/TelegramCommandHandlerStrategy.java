package mate.capsharingapp.service;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.exception.TelegramException;
import mate.capsharingapp.messages.ExceptionMessages;
import mate.capsharingapp.service.impl.AuthCommand;
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
