package com.carsharingapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.rental.RentalFullResponseDto;
import com.carsharingapp.dto.rental.RentalRequestDto;
import com.carsharingapp.dto.rental.RentalResponseDto;
import com.carsharingapp.dto.rental.RentalSetActualReturnDateDto;
import com.carsharingapp.dto.rental.SearchRentalByIsActive;
import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.exception.EntityNotFoundException;
import com.carsharingapp.exception.RentalException;
import com.carsharingapp.mapper.RentalMapper;
import com.carsharingapp.model.Car;
import com.carsharingapp.model.Payment;
import com.carsharingapp.model.Rental;
import com.carsharingapp.model.Role;
import com.carsharingapp.model.User;
import com.carsharingapp.repository.CarRepository;
import com.carsharingapp.repository.PaymentRepository;
import com.carsharingapp.repository.rental.RentalRepository;
import com.carsharingapp.repository.rental.RentalSpecificationBuilder;
import com.carsharingapp.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static final Role USER_ROLE = new Role().setName(Role.RoleName.ROLE_USER);
    private static final User USER = new User()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com")
            .setPassword("password")
            .setRoles(new HashSet<>(Collections.singleton(USER_ROLE)));
    private static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto()
            .setFirstName(USER.getFirstName())
            .setLastName(USER.getLastName())
            .setEmail(USER.getEmail());
    private static final Car CAR = new Car()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final CarFullResponseDto CAR_RESPONSE_DTO = new CarFullResponseDto()
            .setId(CAR.getId())
            .setModel(CAR.getModel())
            .setBrand(CAR.getBrand())
            .setCarType(CAR.getCarType().name())
            .setInventory(CAR.getInventory())
            .setDailyFee(CAR.getDailyFee());
    private static final Rental ACTIVE_RENTAL = new Rental()
            .setId(1L)
            .setUser(USER)
            .setCar(CAR)
            .setRentalDate(LocalDate.of(2024, 7, 20))
            .setReturnDate(LocalDate.of(2024, 7, 29));
    private static final Rental ACTIVE_SECOND_RENTAL = new Rental()
            .setId(2L)
            .setUser(USER)
            .setCar(CAR)
            .setRentalDate(LocalDate.of(2024, 7, 21))
            .setReturnDate(LocalDate.of(2024, 7, 28));
    private static final RentalFullResponseDto ACTIVE_RENTAL_RESPONSE_DTO =
            new RentalFullResponseDto()
            .setId(ACTIVE_RENTAL.getId())
            .setCar(CAR_RESPONSE_DTO)
            .setUser(USER_RESPONSE_DTO)
            .setRentalDate(ACTIVE_RENTAL.getRentalDate())
            .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalResponseDto ACTIVE_RENTAL_SHORT_RESPONSE_DTO =
            new RentalResponseDto()
            .setId(ACTIVE_RENTAL.getId())
            .setCarId(CAR.getId())
            .setUserId(USER.getId())
            .setRentalDate(ACTIVE_RENTAL.getRentalDate())
            .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalResponseDto ACTIVE_SECOND_RENTAL_SHORT_RESPONSE_DTO =
            new RentalResponseDto()
            .setId(ACTIVE_SECOND_RENTAL.getId())
            .setCarId(CAR.getId())
            .setUserId(USER.getId())
            .setRentalDate(ACTIVE_SECOND_RENTAL.getRentalDate())
            .setReturnDate(ACTIVE_SECOND_RENTAL.getReturnDate());
    private static final RentalRequestDto ACTIVE_RENTAL_REQUEST_DTO =
            new RentalRequestDto()
            .setCarId(1L)
            .setRentalDate(ACTIVE_RENTAL.getRentalDate())
            .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalSetActualReturnDateDto RETURN_DATE_DTO =
            new RentalSetActualReturnDateDto()
            .setActualReturnDate(LocalDate.of(2024, 8, 1));
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalSpecificationBuilder rentalSpecificationBuilder;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Save rental with valid data will return the valid rentalDto")
    public void saveRental_WithValidData_ShouldReturnValidRentalDto() {
        when(rentalMapper.toModel(ACTIVE_RENTAL_REQUEST_DTO)).thenReturn(ACTIVE_RENTAL);
        when(rentalMapper.toFullDto(ACTIVE_RENTAL)).thenReturn(ACTIVE_RENTAL_RESPONSE_DTO);
        when(carRepository.findById(ACTIVE_RENTAL_REQUEST_DTO
                .getCarId())).thenReturn(Optional.of(CAR));
        when(rentalRepository.save(ACTIVE_RENTAL)).thenReturn(ACTIVE_RENTAL);
        when(paymentRepository.existsByRentalUserAndStatus(USER, Payment.PaymentStatus.PENDING))
                .thenReturn(false);

        RentalFullResponseDto actual = rentalService.save(USER, ACTIVE_RENTAL_REQUEST_DTO);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ACTIVE_RENTAL_RESPONSE_DTO, actual);
        verify(rentalMapper, times(1)).toModel(ACTIVE_RENTAL_REQUEST_DTO);
        verify(carRepository, times(1)).findById(ACTIVE_RENTAL_REQUEST_DTO.getCarId());
        verify(rentalRepository, times(1)).save(ACTIVE_RENTAL);
        verify(rentalMapper, times(1)).toFullDto(ACTIVE_RENTAL);
    }

    @Test
    @DisplayName("Save rental with no available cars will throw exception")
    public void saveRental_WithNoAvailableCars_ShouldThrowException() {
        Car car = new Car().setId(1L).setInventory(0);

        when(rentalMapper.toModel(ACTIVE_RENTAL_REQUEST_DTO)).thenReturn(ACTIVE_RENTAL);
        when(carRepository.findById(ACTIVE_RENTAL_REQUEST_DTO.getCarId()))
                .thenReturn(Optional.of(car));
        when(paymentRepository.existsByRentalUserAndStatus(USER, Payment.PaymentStatus.PENDING))
                .thenReturn(false);

        RentalException exception = Assertions.assertThrows(RentalException.class,
                () -> rentalService.save(USER, ACTIVE_RENTAL_REQUEST_DTO));

        Assertions.assertEquals("No cars available anymore", exception.getMessage());
        verify(rentalMapper, times(1)).toModel(ACTIVE_RENTAL_REQUEST_DTO);
        verify(carRepository, times(1))
                .findById(ACTIVE_RENTAL_REQUEST_DTO.getCarId());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("Get rental by valid id should return valid RentalResponseDto")
    public void findRentalById_WithValidId_ShouldReturnValidRentalResponseDto() {
        Long id = 1L;

        when(rentalRepository.findByIdAndUser(id, USER)).thenReturn(Optional.of(ACTIVE_RENTAL));
        when(rentalMapper.toFullDto(ACTIVE_RENTAL)).thenReturn(ACTIVE_RENTAL_RESPONSE_DTO);
        RentalFullResponseDto actual = rentalService.findById(USER, id);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ACTIVE_RENTAL_RESPONSE_DTO, actual);
        verify(rentalRepository, times(1)).findByIdAndUser(id, USER);
        verify(rentalMapper, times(1)).toFullDto(ACTIVE_RENTAL);
    }

    @Test
    @DisplayName("Find rental by invalid id will throw the exception")
    public void findRental_WithInvalidRentalId_ShouldThrowException() {
        Long id = 100L;

        when(rentalRepository.findByIdAndUser(id, USER)).thenReturn(Optional.empty());

        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                (() -> rentalService.findById(USER, id))).getMessage();
        String expected = String.format("Can't find rental by id: %d", id);

        Assertions.assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findByIdAndUser(id, USER);
    }

    @Test
    @DisplayName("Find all rentals by active status will return the list of rentals")
    public void findAllRentalsByActiveStatus_ShouldReturnListOfRentals() {
        List<Rental> rentalList = new ArrayList<>();
        rentalList.add(ACTIVE_RENTAL);
        rentalList.add(ACTIVE_SECOND_RENTAL);
        List<RentalResponseDto> rentalResponseDtoList = new ArrayList<>();
        rentalResponseDtoList.add(ACTIVE_RENTAL_SHORT_RESPONSE_DTO);
        rentalResponseDtoList.add(ACTIVE_SECOND_RENTAL_SHORT_RESPONSE_DTO);
        Page<Rental> rentalPage = new PageImpl<>(rentalList);
        SearchRentalByIsActive searchByIsActive =
                new SearchRentalByIsActive().setIsActive("true");

        when(rentalSpecificationBuilder.build(searchByIsActive))
                .thenReturn(Specification.where(null));
        when(rentalRepository.findAll(Specification.where(null),
                Pageable.unpaged())).thenReturn(rentalPage);
        when(rentalMapper.toDto(rentalList.get(0))).thenReturn(rentalResponseDtoList.get(0));
        when(rentalMapper.toDto(rentalList.get(1))).thenReturn(rentalResponseDtoList.get(1));

        Page<RentalResponseDto> actual =
                rentalService.findAllByActiveStatus(searchByIsActive, Pageable.unpaged());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(rentalResponseDtoList, actual.getContent());
        verify(rentalRepository, times(1))
                .findAll(Specification.where(null), Pageable.unpaged());
        verify(rentalMapper, times(1)).toDto(rentalList.get(0));
        verify(rentalMapper, times(1)).toDto(rentalList.get(1));
    }

    @Test
    @DisplayName("Return rental with valid data will return updated rental")
    public void returnRental_WithValidData_ShouldReturnUpdatedRental() {
        when(rentalRepository.findByIdAndUser(anyLong(),
                any(User.class))).thenReturn(Optional.of(ACTIVE_RENTAL));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(CAR));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(ACTIVE_RENTAL_SHORT_RESPONSE_DTO);
        when(rentalRepository.save(any(Rental.class))).thenReturn(ACTIVE_RENTAL);

        RentalResponseDto actual = rentalService.returnRental(USER, 1L, RETURN_DATE_DTO);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ACTIVE_RENTAL_SHORT_RESPONSE_DTO, actual);
        verify(rentalRepository, times(1)).findByIdAndUser(anyLong(), any(User.class));
        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalMapper, times(1)).toDto(any(Rental.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("Return rental with invalid id will throw EntityNotFoundException")
    public void returnRental_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(rentalRepository.findByIdAndUser(anyLong(),
                any(User.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> rentalService.returnRental(USER, 100L, RETURN_DATE_DTO)
        );
        Assertions.assertEquals("Can't find rental by id: 100", exception.getMessage());
        verify(rentalRepository, times(1))
                .findByIdAndUser(anyLong(), any(User.class));
        verify(carRepository, never()).save(any(Car.class));
        verify(rentalMapper, never()).toDto(any(Rental.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
}
