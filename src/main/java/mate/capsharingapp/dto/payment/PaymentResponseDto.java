package mate.capsharingapp.dto.payment;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String sessionId;
    private String sessionUrl;
}
