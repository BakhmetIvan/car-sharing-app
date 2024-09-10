package com.carsharingapp.service.notification.impl;

import com.carsharingapp.messages.NotificationMessages;
import com.carsharingapp.model.Rental;
import com.carsharingapp.repository.rental.RentalRepository;
import com.carsharingapp.service.notification.NotificationService;
import com.carsharingapp.service.notification.OverdueRentalsNotification;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OverdueRentalsNotificationImpl implements OverdueRentalsNotification {
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
                        NotificationMessages.RENTAL_DETAILS_TEMPLATE,
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
                    String.format(NotificationMessages.OVERDUE_RENTAL_NOTIFICATION, rentalDetails)
            );
        }
    }
}
