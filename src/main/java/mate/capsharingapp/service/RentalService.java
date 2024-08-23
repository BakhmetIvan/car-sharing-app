package mate.capsharingapp.service;

import mate.capsharingapp.dto.rental.RentalFullResponseDto;
import mate.capsharingapp.dto.rental.RentalRequestDto;
import mate.capsharingapp.dto.rental.RentalResponseDto;
import mate.capsharingapp.dto.rental.RentalSetActualReturnDateDto;
import mate.capsharingapp.dto.rental.SearchRentalByIsActive;
import mate.capsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalFullResponseDto save(User user, RentalRequestDto requestDto);

    Page<RentalResponseDto> findAllByActiveStatus(SearchRentalByIsActive searchByIsActive,
                                                  Pageable pageable);

    RentalFullResponseDto findById(User user, Long id);

    RentalResponseDto returnRental(User user, Long id, RentalSetActualReturnDateDto returnDateDto);
}
