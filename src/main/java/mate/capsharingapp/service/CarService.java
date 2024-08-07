package mate.capsharingapp.service;

import mate.capsharingapp.dto.car.CarFullResponseDto;
import mate.capsharingapp.dto.car.CarRequestDto;
import mate.capsharingapp.dto.car.CarShortResponseDto;
import mate.capsharingapp.dto.car.CarUpdateInventoryDto;
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
