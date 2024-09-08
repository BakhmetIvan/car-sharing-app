package mate.capsharingapp.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import mate.capsharingapp.dto.payment.PaymentFullResponseDto;
import mate.capsharingapp.dto.payment.PaymentStatusResponseDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.mapper.PaymentMapper;
import mate.capsharingapp.model.Car;
import mate.capsharingapp.model.Payment;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.PaymentRepository;
import mate.capsharingapp.service.impl.StripePaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    private static final String PAYMENT_CANCELED_MESSAGE = "Payment session canceled";
    private static final String PAYMENT_NOT_FOUND_BY_SESSION_EXCEPTION =
            "Can't find payment by sessionId = %s";
    private static final Role USER_ROLE = new Role().setName(Role.RoleName.ROLE_USER);
    private static final Car FIRST_CAR = new Car()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final User USER = new User()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com")
            .setPassword("password")
            .setRoles(new HashSet<>(Collections.singleton(USER_ROLE)));
    private static final Rental RENTAL = new Rental()
            .setId(1L)
            .setUser(USER)
            .setCar(FIRST_CAR)
            .setRentalDate(LocalDate.of(2024, 9, 10))
            .setReturnDate(LocalDate.of(2024, 9, 12))
            .setDeleted(false);
    private static final Payment PAYMENT = new Payment()
            .setId(1L)
            .setType(Payment.PaymentType.PAYMENT)
            .setStatus(Payment.PaymentStatus.PENDING)
            .setRental(RENTAL)
            .setAmountToPay(BigDecimal.valueOf(180))
            .setSessionId("sessionId")
            .setSessionUrl("sessionUrl");
    private static final PaymentFullResponseDto PAYMENT_FULL_RESPONSE_DTO =
            new PaymentFullResponseDto()
                    .setId(PAYMENT.getId())
                    .setType(PAYMENT.getType())
                    .setStatus(PAYMENT.getStatus())
                    .setRentalId(PAYMENT.getRental().getId())
                    .setSessionId(PAYMENT.getSessionId())
                    .setSessionUrl(PAYMENT.getSessionUrl())
                    .setAmountToPay(PAYMENT.getAmountToPay());
    private static final PaymentStatusResponseDto PAYMENT_STATUS_RESPONSE_DTO =
            new PaymentStatusResponseDto()
                    .setStatus(PAYMENT.getStatus())
                    .setSessionId(PAYMENT.getSessionId())
                    .setMessage(PAYMENT_CANCELED_MESSAGE);
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private StripePaymentService paymentService;

    @Test
    @DisplayName("Find all payments by valid user should return a page of payments")
    void findAllByUserId_withValidUser_ShouldReturnPayments() {
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentRepository.findAllByRentalUserId(RENTAL.getUser().getId(), pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(PAYMENT)));
        when(paymentMapper.toFullDto(PAYMENT)).thenReturn(PAYMENT_FULL_RESPONSE_DTO);

        Page<PaymentFullResponseDto> actual = paymentService.findAllByUserId(1L, pageable);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(new PageImpl<>(
                Collections.singletonList(PAYMENT_FULL_RESPONSE_DTO)), actual
        );
        verify(paymentRepository, times(1)).findAllByRentalUserId(1L, pageable);
        verify(paymentMapper, times(1)).toFullDto(PAYMENT);
    }

    @Test
    @DisplayName("Handle cancel payment with valid data should return a message")
    void handleCancel_withValidData_ShouldReturnCanceledMessage() {
        String sessionId = "someSessionId";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(PAYMENT));
        when(paymentRepository.save(PAYMENT))
                .thenReturn(PAYMENT.setStatus(Payment.PaymentStatus.CANCELED));
        when(paymentMapper.toStatusDto(PAYMENT)).thenReturn(PAYMENT_STATUS_RESPONSE_DTO);
        PaymentStatusResponseDto actual = paymentService.handleCancel(sessionId);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(PAYMENT_STATUS_RESPONSE_DTO, actual);
        verify(paymentRepository, times(1)).findBySessionId(sessionId);
        verify(paymentRepository, times(1)).save(PAYMENT);
        verify(paymentMapper, times(1)).toStatusDto(PAYMENT);
    }

    @Test
    @DisplayName("Handle cancel payment with invalid data should throw EntityNotFoundException")
    void handleCancel_withInvalidSessionId_ShouldThrowException() {
        String sessionId = "someSessionId";

        when(paymentRepository.findBySessionId(sessionId))
                .thenReturn(Optional.empty());

        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleCancel(sessionId)).getMessage();
        String expected = String.format(PAYMENT_NOT_FOUND_BY_SESSION_EXCEPTION, sessionId);

        Assertions.assertEquals(expected, actual);
        verify(paymentRepository, times(1)).findBySessionId(sessionId);
    }
}
