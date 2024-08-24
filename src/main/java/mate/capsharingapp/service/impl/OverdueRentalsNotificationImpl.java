package mate.capsharingapp.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.repository.rental.RentalRepository;
import mate.capsharingapp.service.NotificationService;
import mate.capsharingapp.service.OverdueRentalsNotification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OverdueRentalsNotificationImpl implements OverdueRentalsNotification {
    public static final String OVERDUE_RENTAL_NOTIFICATION =
            """
                    Overdue Rental Alert

                    Dear Admins,

                    We would like to inform you about overdue rentals.
                    Below are the details of the rentals and the associated users:

                    %s

                    Please take the necessary actions to address these overdue rentals.

                    Thank you.
                    """;

    public static final String RENTAL_DETAILS_TEMPLATE =
            """
                    Rental Details:
                    - Rental ID: %s
                    - Car Model: %s
                    - Rental Date: %s
                    - Scheduled Return Date: %s
                    User Details:
                    - User ID: %s
                    - User Name: %s %s
                    - Email: %s
                    """;

    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;

    @Scheduled(fixedRate = 86400000)
    @Transactional
    @Override
    public void checkOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());
        if (!overdueRentals.isEmpty()) {
            StringBuilder rentalDetails = new StringBuilder();
            for (Rental rental : overdueRentals) {
                rentalDetails.append(String.format(
                        RENTAL_DETAILS_TEMPLATE,
                        rental.getId(),
                        rental.getCar().getModel(),
                        rental.getRentalDate(),
                        rental.getReturnDate(),
                        rental.getUser().getId(),
                        rental.getUser().getLastName(),
                        rental.getUser().getFirstName(),
                        rental.getUser().getEmail()
                )).append("\n");
            }
            notificationService.sendNotificationToAdmins(
                    String.format(OVERDUE_RENTAL_NOTIFICATION, rentalDetails)
            );
        }
    }
}
