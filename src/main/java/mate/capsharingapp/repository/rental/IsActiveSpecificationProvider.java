package mate.capsharingapp.repository.rental;

import mate.capsharingapp.model.Rental;
import mate.capsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental, Boolean> {
    private static final String IS_ACTIVE_FIELD_NAME = "actualReturnDate";

    @Override
    public String getKey() {
        return IS_ACTIVE_FIELD_NAME;
    }

    @Override
    public Specification<Rental> getSpecification(Boolean param) {
        return (root, query, criteriaBuilder) -> {
            if (param) {
                return criteriaBuilder.isNull(root.get("actualReturnDate"));
            } else {
                return criteriaBuilder.isNotNull(root.get("actualReturnDate"));
            }
        };
    }
}
