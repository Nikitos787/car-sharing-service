package project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.model.Rental;
import project.model.car.Car;
import project.model.payment.Payment;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.CarService;
import project.service.NotificationService;
import project.service.RentalService;
import project.service.UserService;
import project.telegrambot.NotificationBot;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationService {
    private final NotificationBot notificationBot;
    private final UserService userService;
    private final RentalService rentalService;
    private final CarService carService;

    @Override
    public void sendMessageAboutSuccessRent(Rental rental) {
        if (rental.getUser().getChatId() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(rental.getUser().getChatId());
            sendMessage.setText(messageAboutSuccessRent(rental));
            try {
                notificationBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException("Can't sent message to Chat bot");
            }
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    @Override
    public void checkOverdueRentals() {
        LocalDateTime localDate = LocalDateTime.now();
        List<Rental> overdueRent = rentalService.findOverdueRental(localDate);
        for (Rental rental : overdueRent) {
            if (rental.getUser().getChatId() != null) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(rental.getUser().getChatId());
                sendMessage.setText(messageAboutOverdueRent(rental, localDate));
                try {
                    notificationBot.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException("Message about overdue doesn't sent");
                }
            }
        }
    }

    @Override
    public void sendMessageToAdministrators(String message) {
        List<User> managers = userService.findByRoles(RoleName.MANAGER);
        sendMessageToUserBasedByRole(message, managers);
    }

    @Override
    public void sendMessageAboutPaymentToUser(Payment payment, String message) {
        List<User> users = userService.findByRoles(RoleName.CUSTOMER);
        for (User user : users) {
            if (user.getChatId() != null
                    && payment.getRental().getUser().getEmail().equals(user.getEmail())) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText(message);
                try {
                    notificationBot.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(String
                            .format("Message: %s isn't sent to to user's chat", message));
                }
            }
        }
    }

    private void sendMessageToUserBasedByRole(String message, List<User> users) {
        for (User user : users) {
            if (user.getChatId() != null) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText(message);
                try {
                    notificationBot.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(String
                            .format("Message: %s isn't sent to admins chat", message));
                }
            }
        }
    }

    private String messageAboutSuccessRent(Rental rental) {
        Car car = carService.findById(rental.getCar().getId());
        return String.format("%s, you have successfully rented: %s at %s. Please return the "
                        + "car by %s. Your daily fee is: %s",
                rental.getUser().getFirstName(),
                car.getModel(),
                rental.getRentalDate().toString(),
                rental.getReturnDate().toString(),
                car.getDailyFee().toString());
    }

    private String messageAboutOverdueRent(Rental rental, LocalDateTime dateTime) {

        return String.format("You have overdue your rental with id: %d. Payment at %s. "
                        + "Please, pay your fine!",
                rental.getId(),
                dateTime.toString());
    }
}
