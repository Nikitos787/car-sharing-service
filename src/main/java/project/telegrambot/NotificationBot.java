package project.telegrambot;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.model.user.User;
import project.repository.UserRepository;

@Component
public class NotificationBot extends TelegramLongPollingBot {
    private static final String FIRST_MESSAGE = "/start";
    private final UserRepository userRepository;
    @Value("${telegram-bot-name}")
    private String botName;

    public NotificationBot(@Value("${telegram-bot-token}") String botToken,
                           UserRepository userRepository) {
        super(botToken);
        this.userRepository = userRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (messageText.equals(FIRST_MESSAGE)) {
                greetMessage(chatId, update.getMessage().getChat().getFirstName());
            } else {
                Optional<User> userByEmail = userRepository.findByEmail(messageText);
                if (userByEmail.isPresent()) {
                    User user = userByEmail.get();
                    user.setChatId(chatId);
                    userRepository.save(user);
                    thankYouMessage(chatId);
                } else {
                    failMessage(chatId);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sentMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't sent message");
        }
    }

    private void greetMessage(Long chatId, String name) {
        String text = String.format("Hello user: %s, Please send your email", name);
        sentMessage(chatId, text);
    }

    private void failMessage(Long chatId) {
        String text = "User with this email doesn't exist in DB, please check your credential";
        sentMessage(chatId, text);
    }

    private void thankYouMessage(Long chatId) {
        String text = "You are successfully sync with your account";
        sentMessage(chatId, text);
    }
}
