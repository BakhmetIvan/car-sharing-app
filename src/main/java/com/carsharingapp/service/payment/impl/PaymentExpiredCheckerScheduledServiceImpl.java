package com.carsharingapp.service.payment.impl;

import com.carsharingapp.exception.PaymentException;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.model.Payment;
import com.carsharingapp.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentExpiredCheckerScheduledServiceImpl {
    private static final String EXPIRED_SESSION = "expired";
    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 60000)
    public void checkPendingPayments() {
        List<Payment> pendingPayments = paymentRepository
                .findByStatus(Payment.PaymentStatus.PENDING);
        if (!pendingPayments.isEmpty()) {
            for (Payment payment : pendingPayments) {
                updatePaymentStatus(payment);
            }
        }
    }

    public void updatePaymentStatus(Payment payment) {
        try {
            Session session = Session.retrieve(payment.getSessionId());
            String sessionStatus = session.getStatus();
            if (sessionStatus.equals(EXPIRED_SESSION)) {
                payment.setStatus(Payment.PaymentStatus.EXPIRED);
                paymentRepository.save(payment);
            }
        } catch (StripeException e) {
            throw new PaymentException(ExceptionMessages.CHECKING_PAYMENT_EXCEPTION);
        }
    }
}
