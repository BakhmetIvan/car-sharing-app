package mate.capsharingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.capsharingapp.model.Payment;

@Data
@Accessors(chain = true)
public class PaymentFullResponseDto {
    private Long id;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
    private Long rentalId;
    private Payment.PaymentStatus status;
    private Payment.PaymentType type;
}
