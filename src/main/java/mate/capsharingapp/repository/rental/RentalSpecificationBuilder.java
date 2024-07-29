package mate.capsharingapp.repository.rental;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.rental.RentalSearchByIsActiveDto;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.repository.SpecificationBuilder;
import mate.capsharingapp.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private static final String USER_SPECIFICATION = "user";
    private static final String IS_ACTIVE_SPECIFICATION = "actualReturnDate";
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchByIsActiveDto searchByIsActiveDto) {
        Specification<Rental> specification = Specification.where(null);
        if (searchByIsActiveDto.getUserId() != null) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(USER_SPECIFICATION)
                    .getSpecification(searchByIsActiveDto.getUserId()));
        }
        if (searchByIsActiveDto.getIsActive() != null) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IS_ACTIVE_SPECIFICATION)
                    .getSpecification(searchByIsActiveDto.getIsActive()));
        }
        return specification;
    }
}
