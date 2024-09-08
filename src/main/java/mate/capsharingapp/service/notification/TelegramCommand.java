package mate.capsharingapp.service.notification;

public interface TelegramCommand {
    String execute(String messageText, Long chatId);
}
