package mate.capsharingapp.repository.rental;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.rental.SearchRentalByIsActive;
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
    public Specification<Rental> build(SearchRentalByIsActive searchByIsActive) {
        Specification<Rental> specification = Specification.where(null);
        if (searchByIsActive.getUserId() != null) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(USER_SPECIFICATION)
                    .getSpecification(searchByIsActive.getUserId()));
        }
        if (searchByIsActive.getIsActive() != null) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IS_ACTIVE_SPECIFICATION)
                    .getSpecification(searchByIsActive.getIsActive()));
        }
        return specification;
    }
}
