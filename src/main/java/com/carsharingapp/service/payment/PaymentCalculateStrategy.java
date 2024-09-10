package com.carsharingapp.service.payment;

import com.carsharingapp.model.Payment;
import com.carsharingapp.service.payment.impl.DefaultPaymentCalculateService;
import com.carsharingapp.service.payment.impl.FinePaymentCalculateService;

public class PaymentCalculateStrategy {
    public PaymentCalculateService getCalculateServiceByType(Payment.PaymentType paymentType) {
        switch (paymentType) {
            case FINE:
                return new FinePaymentCalculateService();
            default:
                return new DefaultPaymentCalculateService();
        }
    }
}
