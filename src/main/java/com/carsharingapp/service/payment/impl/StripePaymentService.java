package com.carsharingapp.service.payment.impl;

import com.carsharingapp.dto.payment.PaymentFullResponseDto;
import com.carsharingapp.dto.payment.PaymentRequestDto;
import com.carsharingapp.dto.payment.PaymentResponseDto;
import com.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.carsharingapp.exception.EntityNotFoundException;
import com.carsharingapp.exception.PaymentException;
import com.carsharingapp.mapper.PaymentMapper;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.messages.NotificationMessages;
import com.carsharingapp.model.Payment;
import com.carsharingapp.model.Rental;
import com.carsharingapp.model.User;
import com.carsharingapp.repository.PaymentRepository;
import com.carsharingapp.repository.rental.RentalRepository;
import com.carsharingapp.service.notification.NotificationService;
import com.carsharingapp.service.payment.PaymentCalculateStrategy;
import com.carsharingapp.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentService {
    private static final String COMPLETE_SESSION_STATUS = "complete";
    private static final String DOMAIN = "http://ec2-3-84-23-2.compute-1.amazonaws.com";
    private static final String CANCELED_LINK = "/payments/success/{CHECKOUT_SESSION_ID}";
    private static final String SUCCESS_LINK = "/payments/cancel/{CHECKOUT_SESSION_ID}";
    private static final String SESSION_NAME = "Car Rental Payment";
    private static final int DAY_IN_SECONDS = 86400;
    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    @Override
    public PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto, User user) {
        if (paymentRepository.findByRentalIdAndStatusIn(paymentRequestDto.getRentalId(),
                List.of(Payment.PaymentStatus.PAID, Payment.PaymentStatus.PENDING)).isPresent()) {
            throw new PaymentException(ExceptionMessages.PAYMENT_CREATING_EXCEPTION);
        }
        Rental rental = rentalRepository.findById(paymentRequestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ExceptionMessages.RENTAL_NOT_FOUND_EXCEPTION,
                                paymentRequestDto.getRentalId()))
                );
        if (!rental.getUser().getId().equals(user.getId())) {
            throw new PaymentException(ExceptionMessages.RENTAL_ACCESS_EXCEPTION);
        }
        PaymentCalculateStrategy calculateStrategy = new PaymentCalculateStrategy();
        Payment.PaymentType requestPaymentType =
                Payment.PaymentType.valueOf(paymentRequestDto.getPaymentType().toUpperCase());
        long amountToPay = calculateStrategy
                .getCalculateServiceByType(requestPaymentType)
                .calculateAmountToPay(rental).longValue();
        Session session = createStripeSession(amountToPay).orElseThrow(
                () -> new PaymentException(ExceptionMessages.PAYMENT_CREATING_EXCEPTION)
        );
        Payment payment = new Payment()
                .setRental(rental)
                .setAmountToPay(BigDecimal.valueOf(amountToPay))
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setStatus(Payment.PaymentStatus.PENDING)
                .setType(Payment.PaymentType.valueOf(paymentRequestDto
                        .getPaymentType().toUpperCase()));
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentFullResponseDto> findAllByUserId(Long id, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(id, pageable)
                .map(paymentMapper::toFullDto);
    }

    @Transactional
    @Override
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        try {
            Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                    () -> new EntityNotFoundException(
                            String.format(ExceptionMessages.PAYMENT_NOT_FOUND_BY_SESSION_EXCEPTION,
                                    sessionId))
            );
            Session session = Session.retrieve(sessionId);
            if (session.getStatus().equals(COMPLETE_SESSION_STATUS)) {
                payment.setStatus(Payment.PaymentStatus.PAID);
                Rental rental = payment.getRental();
                notificationService.sendNotificationToAdmins(
                        String.format(
                                NotificationMessages.PAYMENT_SUCCESS_NOTIFICATION,
                                rental.getId(),
                                rental.getCar().getModel(),
                                rental.getRentalDate(),
                                rental.getReturnDate(),
                                payment.getAmountToPay(),
                                rental.getUser().getId(),
                                rental.getUser().getLastName(),
                                rental.getUser().getFirstName(),
                                rental.getUser().getEmail())
                );
                paymentRepository.save(payment);
                return paymentMapper.toStatusDto(payment)
                        .setMessage(NotificationMessages.SUCCESS_COMPLETE_MESSAGE);
            }
            return paymentMapper.toStatusDto(payment)
                    .setMessage(NotificationMessages.SUCCESS_NOT_COMPLETE_MESSAGE);
        } catch (StripeException e) {
            throw new PaymentException(
                    String.format(ExceptionMessages.PAYMENT_SESSION_EXCEPTION, sessionId));
        }
    }

    @Transactional
    @Override
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.PAYMENT_NOT_FOUND_BY_SESSION_EXCEPTION,
                                sessionId))
        );
        payment.setStatus(Payment.PaymentStatus.CANCELED);
        return paymentMapper.toStatusDto(paymentRepository.save(payment))
                .setMessage(NotificationMessages.PAYMENT_CANCELED_MESSAGE);
    }

    private Optional<Session> createStripeSession(long amountToPay) {
        try {
            SessionCreateParams params = buildSessionCreateParams(amountToPay);
            return Optional.of(Session.create(params));
        } catch (Exception ex) {
            throw new PaymentException(ExceptionMessages.PAYMENT_CREATING_EXCEPTION);
        }
    }

    private SessionCreateParams buildSessionCreateParams(long amountToPay) {
        return SessionCreateParams.builder()
                .setExpiresAt(Instant.now().getEpochSecond() + DAY_IN_SECONDS)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(DOMAIN + SUCCESS_LINK)
                .setCancelUrl(DOMAIN + CANCELED_LINK)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountToPay * 100)
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(SESSION_NAME)
                                                                .build())
                                                .build())
                                .build())
                .build();
    }
}
