package mate.capsharingapp.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T, Y> {
    String getKey();

    Specification<T> getSpecification(Y param);
}
