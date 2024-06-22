package mate.capsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.CarFullResponseDto;
import mate.capsharingapp.dto.CarRequestDto;
import mate.capsharingapp.dto.CarShortResponseDto;
import mate.capsharingapp.dto.CarUpdateInventoryDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.mapper.CarMapper;
import mate.capsharingapp.model.Car;
import mate.capsharingapp.repository.CarRepository;
import mate.capsharingapp.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private static final String CAR_NOT_FOUND_EXCEPTION = "Can't find car by id = %d";
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
                () -> new EntityNotFoundException(String.format(CAR_NOT_FOUND_EXCEPTION, id))
        );
        carMapper.updateCarFromDto(car, requestDto);
        return carMapper.toFullDto(carRepository.save(car));
    }

    @Override
    public CarFullResponseDto updateInventory(Long id, CarUpdateInventoryDto updateInventoryDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format(CAR_NOT_FOUND_EXCEPTION, id))
        );
        carMapper.patchCarFromDto(car, updateInventoryDto);
        return carMapper.toFullDto(car);
    }

    @Override
    public CarFullResponseDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format(CAR_NOT_FOUND_EXCEPTION, id))
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
                () -> new EntityNotFoundException(String.format(CAR_NOT_FOUND_EXCEPTION, id))
        );
        carRepository.delete(car);
    }
}
