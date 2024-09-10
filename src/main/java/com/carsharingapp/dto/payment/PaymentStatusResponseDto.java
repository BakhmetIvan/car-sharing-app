package com.carsharingapp.dto.payment;

import com.carsharingapp.model.Payment;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentStatusResponseDto {
    private String message;
    private String sessionId;
    private Payment.PaymentStatus status;
}
