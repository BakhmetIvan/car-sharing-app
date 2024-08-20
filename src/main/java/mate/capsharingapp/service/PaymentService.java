package mate.capsharingapp.service;

import mate.capsharingapp.dto.payment.PaymentFullResponseDto;
import mate.capsharingapp.dto.payment.PaymentRequestDto;
import mate.capsharingapp.dto.payment.PaymentResponseDto;
import mate.capsharingapp.dto.payment.PaymentStatusResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto);

    Page<PaymentFullResponseDto> findAllByUserId(Long id, Pageable pageable);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
