package mate.capsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.rental.RentalFullResponseDto;
import mate.capsharingapp.dto.rental.RentalRequestDto;
import mate.capsharingapp.dto.rental.RentalResponseDto;
import mate.capsharingapp.dto.rental.RentalSearchByIsActiveDto;
import mate.capsharingapp.dto.rental.RentalSetActualReturnDateDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.exception.RentalException;
import mate.capsharingapp.mapper.RentalMapper;
import mate.capsharingapp.model.Car;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.CarRepository;
import mate.capsharingapp.repository.SpecificationBuilder;
import mate.capsharingapp.repository.rental.RentalRepository;
import mate.capsharingapp.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final String CAR_NOT_FOUND_EXCEPTION = "Can't find car by id: %d";
    private static final String RENTAL_NOT_FOUND_EXCEPTION = "Can't find rental by id: %d";
    private static final String NO_CARS_AVAILABLE_EXCEPTION = "No cars available anymore";
    private final SpecificationBuilder<Rental> rentalSpecificationBuilder;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    @Override
    public RentalResponseDto save(User user, RentalRequestDto requestDto) {
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setUser(user);
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(CAR_NOT_FOUND_EXCEPTION, requestDto.getCarId()))
        );
        if (car.getInventory() < 1) {
            throw new RentalException(NO_CARS_AVAILABLE_EXCEPTION);
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Transactional
    @Override
    public Page<RentalResponseDto> findAllByActiveStatus(
            RentalSearchByIsActiveDto searchByIsActiveDto,
            Pageable pageable
    ) {
        Specification<Rental> specification = rentalSpecificationBuilder.build(searchByIsActiveDto);
        return rentalRepository.findAll(specification, pageable)
                .map(rentalMapper::toDto);
    }

    @Override
    public RentalFullResponseDto findById(User user, Long id) {
        Rental rental = rentalRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(RENTAL_NOT_FOUND_EXCEPTION, id)
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
                        String.format(RENTAL_NOT_FOUND_EXCEPTION, id)
                )
        );
        Long carId = rental.getCar().getId();
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(CAR_NOT_FOUND_EXCEPTION, carId))
        );
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rentalMapper.setActualReturnDateFromDto(rental, returnDateDto);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
