package mate.capsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.payment.PaymentFullResponseDto;
import mate.capsharingapp.dto.payment.PaymentRequestDto;
import mate.capsharingapp.dto.payment.PaymentResponseDto;
import mate.capsharingapp.dto.payment.PaymentStatusResponseDto;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Payment controller", description = "Endpoints for operations with payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new payment",
            description = "Allows a user's to create a new payment")
    public PaymentResponseDto createPayment(Authentication authentication,
                                            @RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    @Operation(summary = "Find all payments",
            description = "Returns a page of payments by user id")
    public Page<PaymentFullResponseDto> findAll(Authentication authentication,
                                                @RequestParam @Positive Long userId,
                                                @PageableDefault Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().stream()
                .noneMatch(role ->
                        role.getAuthority().equals(Role.RoleName.ROLE_MANAGER.name()))) {
            return paymentService.findAllByUserId(userId, pageable);
        }
        return paymentService.findAllByUserId(user.getId(), pageable);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/success/{sessionId}")
    @Operation(summary = "Handle success payment",
            description = "Returns a message from success payment")
    public PaymentStatusResponseDto handleSuccess(@PathVariable @NotBlank String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/cancel/{sessionId}")
    @Operation(summary = "Handle canceled payment",
            description = "Returns a message from canceled payment")
    public PaymentStatusResponseDto handleCancel(@PathVariable @NotBlank String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
