package com.carsharingapp.service.payment.impl;

import com.carsharingapp.model.Rental;
import com.carsharingapp.service.payment.PaymentCalculateService;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentCalculateService implements PaymentCalculateService {
    @Override
    public BigDecimal calculateAmountToPay(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));
    }
}
