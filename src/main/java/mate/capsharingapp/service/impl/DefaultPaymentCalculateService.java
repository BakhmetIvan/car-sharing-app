package mate.capsharingapp.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.service.PaymentCalculateService;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentCalculateService implements PaymentCalculateService {
    @Override
    public BigDecimal calculateAmountToPay(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));
    }
}
