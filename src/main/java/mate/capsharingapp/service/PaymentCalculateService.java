package mate.capsharingapp.service;

import java.math.BigDecimal;
import mate.capsharingapp.model.Rental;

public interface PaymentCalculateService {
    BigDecimal calculateAmountToPay(Rental rental);
}
