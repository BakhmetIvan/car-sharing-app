package com.carsharingapp.service;

import com.carsharingapp.dto.rental.RentalFullResponseDto;
import com.carsharingapp.dto.rental.RentalRequestDto;
import com.carsharingapp.dto.rental.RentalResponseDto;
import com.carsharingapp.dto.rental.RentalSetActualReturnDateDto;
import com.carsharingapp.dto.rental.SearchRentalByIsActive;
import com.carsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalFullResponseDto save(User user, RentalRequestDto requestDto);

    Page<RentalResponseDto> findAllByActiveStatus(SearchRentalByIsActive searchByIsActive,
                                                  Pageable pageable);

    RentalFullResponseDto findById(User user, Long id);

    RentalResponseDto returnRental(User user, Long id, RentalSetActualReturnDateDto returnDateDto);
}
