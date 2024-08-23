package mate.capsharingapp.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentRequestDto {
    private Long rentalId;
    private String paymentType;
}
