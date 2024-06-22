package mate.capsharingapp.mapper;

import mate.capsharingapp.config.MapperConfig;
import mate.capsharingapp.dto.CarFullResponseDto;
import mate.capsharingapp.dto.CarRequestDto;
import mate.capsharingapp.dto.CarShortResponseDto;
import mate.capsharingapp.dto.CarUpdateInventoryDto;
import mate.capsharingapp.model.Car;
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
