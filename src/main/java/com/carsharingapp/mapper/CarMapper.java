package com.carsharingapp.mapper;

import com.carsharingapp.config.MapperConfig;
import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.car.CarRequestDto;
import com.carsharingapp.dto.car.CarShortResponseDto;
import com.carsharingapp.dto.car.CarUpdateInventoryDto;
import com.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CarRequestDto requestDto);

    CarShortResponseDto toShortDto(Car car);

    CarFullResponseDto toFullDto(Car car);

    void updateCarFromDto(@MappingTarget Car car, CarRequestDto requestDto);

    void patchCarFromDto(@MappingTarget Car car, CarUpdateInventoryDto updateInventoryDto);
}
