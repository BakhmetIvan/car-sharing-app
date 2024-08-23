package mate.capsharingapp.service;

import mate.capsharingapp.model.Payment;
import mate.capsharingapp.service.impl.DefaultPaymentCalculateService;
import mate.capsharingapp.service.impl.FinePaymentCalculateService;

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
