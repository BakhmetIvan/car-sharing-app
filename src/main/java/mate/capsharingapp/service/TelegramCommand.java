package mate.capsharingapp.service;

public interface TelegramCommand {
    String execute(String messageText, Long chatId);
}
