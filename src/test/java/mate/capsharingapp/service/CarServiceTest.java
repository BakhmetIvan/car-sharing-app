package mate.capsharingapp.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.capsharingapp.dto.CarFullResponseDto;
import mate.capsharingapp.dto.CarRequestDto;
import mate.capsharingapp.dto.CarShortResponseDto;
import mate.capsharingapp.dto.CarUpdateInventoryDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.mapper.CarMapper;
import mate.capsharingapp.model.Car;
import mate.capsharingapp.repository.CarRepository;
import mate.capsharingapp.service.impl.CarServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    private static final String CAR_NOT_FOUND_EXCEPTION = "Can't find car by id = %d";
    private static final Car FIRST_CAR = new Car()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final Car SECOND_CAR = new Car()
            .setId(2L)
            .setModel("A6")
            .setBrand("AUDI")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(14)
            .setDailyFee(BigDecimal.valueOf(80));
    private static final CarRequestDto FIRST_CAR_REQUEST_DTO =
            new CarRequestDto()
            .setModel("M5")
            .setBrand("BMW")
            .setCarType("SEDAN")
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final CarFullResponseDto FIRST_CAR_FULL_RESPONSE_DTO =
            new CarFullResponseDto()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType("SEDAN")
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final CarShortResponseDto FIRST_CAR_SHORT_RESPONSE_DTO =
            new CarShortResponseDto()
            .setId(1L)
            .setModel("M5")
            .setCarType("SEDAN");
    private static final CarShortResponseDto SECOND_CAR_SHORT_RESPONSE_DTO =
            new CarShortResponseDto()
            .setId(2L)
            .setModel("A6")
            .setCarType("SEDAN");
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Save car with valid data will return the valid CarResponseDto")
    public void saveCar_WithValidData_ShouldReturnValidCarResponseDto() {
        CarRequestDto requestDto = FIRST_CAR_REQUEST_DTO;

        when(carMapper.toModel(requestDto)).thenReturn(FIRST_CAR);
        when(carMapper.toFullDto(FIRST_CAR)).thenReturn(FIRST_CAR_FULL_RESPONSE_DTO);
        when(carRepository.save(FIRST_CAR)).thenReturn(FIRST_CAR);
        CarFullResponseDto actual = carService.save(requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(FIRST_CAR_FULL_RESPONSE_DTO, actual);
        verify(carMapper, times(1)).toModel(requestDto);
        verify(carRepository, times(1)).save(FIRST_CAR);
        verify(carMapper, times(1)).toFullDto(FIRST_CAR);
    }

    @Test
    @DisplayName("Find car by id should return the valid CarFullResponseDto")
    public void findById_WithValidData_ShouldReturnValidCarResponseDto() {
        Long id = 1L;

        when(carRepository.findById(id)).thenReturn(Optional.of(FIRST_CAR));
        when(carMapper.toFullDto(FIRST_CAR)).thenReturn(FIRST_CAR_FULL_RESPONSE_DTO);
        CarFullResponseDto actual = carService.getById(id);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(FIRST_CAR_FULL_RESPONSE_DTO, actual);
        verify(carRepository, times(1)).findById(id);
        verify(carMapper, times(1)).toFullDto(FIRST_CAR);
    }

    @Test
    @DisplayName("Find car by invalid id should throw a exception")
    public void findById_WithInvalidId_ShouldThrowException() {
        Long id = 100L;

        when(carRepository.findById(id)).thenReturn(Optional.empty());

        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                (() -> carService.getById(id))).getMessage();
        String expected = String.format(CAR_NOT_FOUND_EXCEPTION, id);

        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Find all cars will return the page of cars")
    public void findAllCars_ShouldReturnPageOfCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(FIRST_CAR);
        cars.add(SECOND_CAR);
        List<CarShortResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(FIRST_CAR_SHORT_RESPONSE_DTO);
        responseDtoList.add(SECOND_CAR_SHORT_RESPONSE_DTO);
        Page<Car> carPage = new PageImpl<>(cars);
        Page<CarShortResponseDto> expected = new PageImpl<>(responseDtoList);

        when(carRepository.findAll(Pageable.unpaged())).thenReturn(carPage);
        when(carMapper.toShortDto(cars.get(0))).thenReturn(expected.getContent().get(0));
        when(carMapper.toShortDto(cars.get(1))).thenReturn(expected.getContent().get(1));
        Page<CarShortResponseDto> actual = carService.getAll(Pageable.unpaged());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(1)).findAll(Pageable.unpaged());
        verify(carMapper, times(1)).toShortDto(cars.get(0));
        verify(carMapper, times(1)).toShortDto(cars.get(1));
    }

    @Test
    @DisplayName("Update car by valid id and data should return the updated car")
    public void updateCarById_WithValidData_ShouldReturnUpdatedCar() {
        Long id = 1L;
        Car updatedCar = FIRST_CAR.setModel("M3");
        CarRequestDto requestDto = FIRST_CAR_REQUEST_DTO.setModel("M3");
        CarFullResponseDto updatedCarDto = FIRST_CAR_FULL_RESPONSE_DTO.setModel("M3");

        when(carRepository.findById(id)).thenReturn(Optional.of(FIRST_CAR));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toFullDto(updatedCar)).thenReturn(updatedCarDto);
        CarFullResponseDto actual = carService.update(id, requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(updatedCarDto, actual);
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).save(updatedCar);
        verify(carMapper, times(1)).toFullDto(updatedCar);
    }

    @Test
    @DisplayName("Update car by invalid id should should throw exception")
    public void updateCarById_WithInvalidId_ShouldThrowException() {
        Long id = 100L;
        CarRequestDto requestDto = FIRST_CAR_REQUEST_DTO.setModel("M3");

        when(carRepository.findById(id)).thenReturn(Optional.empty());
        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                (() -> carService.update(id, requestDto))).getMessage();
        String expected = String.format(CAR_NOT_FOUND_EXCEPTION, id);

        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Update car inventory by valid id should update the car inventory")
    public void updateCarInventoryById_WithValidData_ShouldReturnUpdatedCar() {
        Long id = 1L;
        Car updatedCar = FIRST_CAR.setInventory(1);
        CarUpdateInventoryDto requestDto = new CarUpdateInventoryDto();
        requestDto.setInventory(1);
        CarFullResponseDto updatedCarDto = FIRST_CAR_FULL_RESPONSE_DTO.setInventory(1);

        when(carRepository.findById(id)).thenReturn(Optional.of(FIRST_CAR));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toFullDto(updatedCar)).thenReturn(updatedCarDto);
        CarFullResponseDto actual = carService.updateInventory(id, requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(updatedCarDto, actual);
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).save(updatedCar);
        verify(carMapper, times(1)).toFullDto(updatedCar);
    }

    @Test
    @DisplayName("Update car inventory by invalid id should throw a exception")
    public void updateCarInventoryById_WithInvalidId_ShouldThrowException() {
        Long id = 100L;
        CarUpdateInventoryDto requestDto = new CarUpdateInventoryDto();
        requestDto.setInventory(1);

        when(carRepository.findById(id)).thenReturn(Optional.empty());
        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                (() -> carService.updateInventory(id, requestDto))).getMessage();
        String expected = String.format(CAR_NOT_FOUND_EXCEPTION, id);

        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Delete car by valid id should delete the car from db")
    public void deleteCar_WithValidId_ShouldDeleteCar() {
        Long id = 1L;

        when(carRepository.findById(id)).thenReturn(Optional.of(FIRST_CAR));
        carService.delete(id);

        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).delete(FIRST_CAR);
    }

    @Test
    @DisplayName("Delete car by invalid id should throw exception")
    public void deleteCar_WithInvalidId_ShouldThrowException() {
        Long id = 100L;

        when(carRepository.findById(id)).thenReturn(Optional.empty());
        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                (() -> carService.delete(id))).getMessage();
        String expected = String.format(CAR_NOT_FOUND_EXCEPTION, id);

        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(id);
    }
}
