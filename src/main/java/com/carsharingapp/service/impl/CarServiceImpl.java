package com.carsharingapp.service.impl;

import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.car.CarRequestDto;
import com.carsharingapp.dto.car.CarShortResponseDto;
import com.carsharingapp.dto.car.CarUpdateInventoryDto;
import com.carsharingapp.exception.EntityNotFoundException;
import com.carsharingapp.mapper.CarMapper;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.model.Car;
import com.carsharingapp.repository.CarRepository;
import com.carsharingapp.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarFullResponseDto save(CarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toFullDto(carRepository.save(car));
    }

    @Override
    public CarFullResponseDto update(Long id, CarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION, id))
        );
        carMapper.updateCarFromDto(car, requestDto);
        return carMapper.toFullDto(carRepository.save(car));
    }

    @Override
    public CarFullResponseDto updateInventory(Long id, CarUpdateInventoryDto updateInventoryDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION, id))
        );
        carMapper.patchCarFromDto(car, updateInventoryDto);
        return carMapper.toFullDto(carRepository.save(car));
    }

    @Override
    public CarFullResponseDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION, id))
        );
        return carMapper.toFullDto(car);
    }

    @Override
    public Page<CarShortResponseDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toShortDto);
    }

    @Override
    public void delete(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION, id))
        );
        carRepository.delete(car);
    }
}
