package mate.capsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.rental.RentalFullResponseDto;
import mate.capsharingapp.dto.rental.RentalRequestDto;
import mate.capsharingapp.dto.rental.RentalResponseDto;
import mate.capsharingapp.dto.rental.RentalSetActualReturnDateDto;
import mate.capsharingapp.dto.rental.SearchRentalByIsActive;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.exception.RentalException;
import mate.capsharingapp.mapper.RentalMapper;
import mate.capsharingapp.messages.ExceptionMessages;
import mate.capsharingapp.messages.NotificationMessages;
import mate.capsharingapp.model.Car;
import mate.capsharingapp.model.Payment;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.CarRepository;
import mate.capsharingapp.repository.PaymentRepository;
import mate.capsharingapp.repository.SpecificationBuilder;
import mate.capsharingapp.repository.rental.RentalRepository;
import mate.capsharingapp.service.RentalService;
import mate.capsharingapp.service.notification.impl.TelegramNotificationServiceImpl;
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
