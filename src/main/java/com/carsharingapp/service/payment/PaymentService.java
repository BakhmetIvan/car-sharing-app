package com.carsharingapp.service.payment;

import com.carsharingapp.dto.payment.PaymentFullResponseDto;
import com.carsharingapp.dto.payment.PaymentRequestDto;
import com.carsharingapp.dto.payment.PaymentResponseDto;
import com.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.carsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto, User user);

    Page<PaymentFullResponseDto> findAllByUserId(Long id, Pageable pageable);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
