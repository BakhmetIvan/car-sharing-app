package com.carsharingapp.service.payment;

import com.carsharingapp.model.Rental;
import java.math.BigDecimal;

public interface PaymentCalculateService {
    BigDecimal calculateAmountToPay(Rental rental);
}
