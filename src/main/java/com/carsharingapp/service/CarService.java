package com.carsharingapp.service;

import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.car.CarRequestDto;
import com.carsharingapp.dto.car.CarShortResponseDto;
import com.carsharingapp.dto.car.CarUpdateInventoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarFullResponseDto save(CarRequestDto requestDto);

    CarFullResponseDto update(Long id, CarRequestDto requestDto);

    CarFullResponseDto updateInventory(Long id, CarUpdateInventoryDto updateInventoryDto);

    CarFullResponseDto getById(Long id);

    Page<CarShortResponseDto> getAll(Pageable pageable);

    void delete(Long id);
}
