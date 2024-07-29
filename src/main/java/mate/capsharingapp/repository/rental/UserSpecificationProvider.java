package mate.capsharingapp.repository.rental;

import mate.capsharingapp.model.Rental;
import mate.capsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    private static final String USER_FIELD_NAME = "user";

    @Override
    public String getKey() {
        return USER_FIELD_NAME;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get(USER_FIELD_NAME).get("id"), param);
    }
}
