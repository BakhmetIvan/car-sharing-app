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
    private final SpecificationProviderManager<Rental, Object> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchByIsActiveDto searchByIsActiveDto) {
        Specification<Rental> specification = Specification.where(null);
        if (searchByIsActiveDto.getUserId() != null) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider("userId")
                    .getSpecification(searchByIsActiveDto.getUserId()));
        }
        if (searchByIsActiveDto.isActive()) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider("isActive")
                    .getSpecification(searchByIsActiveDto.isActive()));
        }
        return specification;
    }
}
