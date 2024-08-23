package mate.capsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.rental.RentalFullResponseDto;
import mate.capsharingapp.dto.rental.RentalRequestDto;
import mate.capsharingapp.dto.rental.RentalResponseDto;
import mate.capsharingapp.dto.rental.RentalSetActualReturnDateDto;
import mate.capsharingapp.dto.rental.SearchRentalByIsActive;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
@Tag(name = "Rental controller", description = "Endpoints for operations with rentals")
public class RentalController {
    private static final String ACCESS_DENIED_EXCEPTION =
            "You are not allowed to view rentals of another user";
    private final RentalService rentalService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental",
            description = "Allows a user to create a new rental")
    public RentalFullResponseDto createRental(Authentication authentication,
                                          @RequestBody @Valid RentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.save(user, requestDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    @Operation(summary = "Get page of rentals",
            description = "Allows a user to find all his rentals filtered by isActive"
                    + "and allows manager to find all rentals for specific user or for all users")
    public Page<RentalResponseDto> findAllByActiveStatus(
            Authentication authentication,
            String userId, @NotBlank String isActive,
            @PageableDefault Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        SearchRentalByIsActive searchByIsActive = new SearchRentalByIsActive()
                .setIsActive(isActive);
        boolean isManager = user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.RoleName.ROLE_MANAGER.name()));
        if (!isManager && userId != null) {
            throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }
        if (!isManager) {
            searchByIsActive.setUserId(user.getId().toString());
        } else {
            searchByIsActive.setUserId(userId != null ? userId : user.getId().toString());
        }
        return rentalService.findAllByActiveStatus(searchByIsActive, pageable);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get rental by id",
            description = "Allows a user to find rental by id")
    public RentalFullResponseDto findRentalById(Authentication authentication,
                                                @PathVariable @Positive Long id) {
        User user = (User) authentication.getPrincipal();
        return rentalService.findById(user, id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/return")
    @Operation(summary = "Return rental",
            description = "Allows a user to return a rental")
    public RentalResponseDto returnRental(
            Authentication authentication,
            @RequestBody @Valid RentalSetActualReturnDateDto returnDateDto,
            @PathVariable @Positive Long id
    ) {
        User user = (User) authentication.getPrincipal();
        return rentalService.returnRental(user, id, returnDateDto);
    }
}
