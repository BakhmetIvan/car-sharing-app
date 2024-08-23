package mate.capsharingapp.repository;

import java.util.List;
import java.util.Optional;
import mate.capsharingapp.model.Payment;
import mate.capsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(Payment.PaymentStatus status);

    Page<Payment> findAllByRentalUserId(Long id, Pageable pageable);

    boolean existsByRentalUserAndStatus(User user, Payment.PaymentStatus status);

    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findByRentalIdAndStatusIn(Long id,
                                                        List<Payment.PaymentStatus> statusList);
}
