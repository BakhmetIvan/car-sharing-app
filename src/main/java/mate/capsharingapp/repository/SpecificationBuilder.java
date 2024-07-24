package mate.capsharingapp.repository;

import mate.capsharingapp.dto.rental.RentalSearchByIsActiveDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchByIsActiveDto searchByIsActiveDto);
}
