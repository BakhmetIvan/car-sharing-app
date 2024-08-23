package mate.capsharingapp.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.service.PaymentCalculateService;
import org.springframework.stereotype.Service;

@Service
public class FinePaymentCalculateService implements PaymentCalculateService {
    private static final float FINE_MULTIPLIER = 1.1f;

    @Override
    public BigDecimal calculateAmountToPay(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
        return rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(days))
                .multiply(BigDecimal.valueOf(FINE_MULTIPLIER));
    }
}
