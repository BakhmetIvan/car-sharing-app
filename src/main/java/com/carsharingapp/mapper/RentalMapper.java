package com.carsharingapp.mapper;

import com.carsharingapp.config.MapperConfig;
import com.carsharingapp.dto.rental.RentalFullResponseDto;
import com.carsharingapp.dto.rental.RentalRequestDto;
import com.carsharingapp.dto.rental.RentalResponseDto;
import com.carsharingapp.dto.rental.RentalSetActualReturnDateDto;
import com.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    Rental toModel(RentalRequestDto requestDto);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    RentalFullResponseDto toFullDto(Rental rental);

    void setActualReturnDateFromDto(@MappingTarget Rental rental,
                                    RentalSetActualReturnDateDto returnDateDto);
}
