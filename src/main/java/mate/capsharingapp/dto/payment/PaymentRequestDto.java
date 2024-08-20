package mate.capsharingapp.dto.payment;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long rentalId;
    private String paymentType;
}
