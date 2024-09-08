package mate.capsharingapp.service.payment;

import mate.capsharingapp.model.Payment;
import mate.capsharingapp.service.payment.impl.DefaultPaymentCalculateService;
import mate.capsharingapp.service.payment.impl.FinePaymentCalculateService;

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
