package mate.capsharingapp.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.capsharingapp.exception.PaymentException;
import mate.capsharingapp.model.Payment;
import mate.capsharingapp.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentExpiredCheckerScheduledServiceImpl {
    private static final String CHECKING_PAYMENT_EXCEPTION =
            "Error while checking payment for expiration";
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
            throw new PaymentException(CHECKING_PAYMENT_EXCEPTION);
        }
    }
}
