package com.carsharingapp.service.impl;

import com.carsharingapp.dto.rental.RentalFullResponseDto;
import com.carsharingapp.dto.rental.RentalRequestDto;
import com.carsharingapp.dto.rental.RentalResponseDto;
import com.carsharingapp.dto.rental.RentalSetActualReturnDateDto;
import com.carsharingapp.dto.rental.SearchRentalByIsActive;
import com.carsharingapp.exception.EntityNotFoundException;
import com.carsharingapp.exception.RentalException;
import com.carsharingapp.mapper.RentalMapper;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.messages.NotificationMessages;
import com.carsharingapp.model.Car;
import com.carsharingapp.model.Payment;
import com.carsharingapp.model.Rental;
import com.carsharingapp.model.User;
import com.carsharingapp.repository.CarRepository;
import com.carsharingapp.repository.PaymentRepository;
import com.carsharingapp.repository.SpecificationBuilder;
import com.carsharingapp.repository.rental.RentalRepository;
import com.carsharingapp.service.RentalService;
import com.carsharingapp.service.notification.impl.TelegramNotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final TelegramNotificationServiceImpl telegramNotificationService;
    private final SpecificationBuilder<Rental> rentalSpecificationBuilder;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    @Override
    public RentalFullResponseDto save(User user, RentalRequestDto requestDto) {
        checkForUnpaidRentals(user);
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setUser(user);
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION,
                                requestDto.getCarId()))
        );
        if (car.getInventory() < 1) {
            throw new RentalException(ExceptionMessages.NO_CARS_AVAILABLE_EXCEPTION);
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(car);
        if (!(user.getTgChatId() == null)) {
            String message = String.format(
                    NotificationMessages.RENTAL_CREATE_NOTIFICATION,
                    rental.getId(),
                    rental.getCar().getModel(),
                    rental.getRentalDate(),
                    rental.getReturnDate(),
                    user.getId(),
                    user.getLastName(),
                    user.getFirstName(),
                    user.getEmail()
            );
            telegramNotificationService.sendNotificationToAdmins(message);
        }
        return rentalMapper.toFullDto(rentalRepository.save(rental));
    }

    @Transactional
    @Override
    public Page<RentalResponseDto> findAllByActiveStatus(
            SearchRentalByIsActive searchByIsActive,
            Pageable pageable
    ) {
        Specification<Rental> specification = rentalSpecificationBuilder.build(searchByIsActive);
        return rentalRepository.findAll(specification, pageable)
                .map(rentalMapper::toDto);
    }

    @Override
    public RentalFullResponseDto findById(User user, Long id) {
        Rental rental = rentalRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.RENTAL_NOT_FOUND_EXCEPTION, id)
                )
        );
        return rentalMapper.toFullDto(rental);
    }

    @Transactional
    @Override
    public RentalResponseDto returnRental(User user,
                                          Long id,
                                          RentalSetActualReturnDateDto returnDateDto) {
        Rental rental = rentalRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.RENTAL_NOT_FOUND_EXCEPTION, id)
                )
        );
        if (rental.getActualReturnDate() != null) {
            throw new RentalException(
                    String.format(ExceptionMessages.RENTAL_ALREADY_RETURNED_EXCEPTION,
                            rental.getId())
            );
        }
        Long carId = rental.getCar().getId();
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION, carId))
        );
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rentalMapper.setActualReturnDateFromDto(rental, returnDateDto);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    private void checkForUnpaidRentals(User user) {
        boolean hasUnpaidRentals = paymentRepository
                .existsByRentalUserAndStatus(user, Payment.PaymentStatus.PENDING);
        if (hasUnpaidRentals) {
            throw new RentalException(ExceptionMessages.UNPAID_PAYMENT_EXCEPTION);
        }
    }
}
