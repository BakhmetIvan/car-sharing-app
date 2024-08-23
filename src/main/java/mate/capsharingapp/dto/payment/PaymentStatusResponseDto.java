package mate.capsharingapp.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;
import mate.capsharingapp.model.Payment;

@Data
@Accessors(chain = true)
public class PaymentStatusResponseDto {
    private String message;
    private String sessionId;
    private Payment.PaymentStatus status;
}
