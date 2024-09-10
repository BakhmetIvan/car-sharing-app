package com.carsharingapp.repository;

import com.carsharingapp.dto.rental.SearchRentalByIsActive;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(SearchRentalByIsActive searchByIsActive);
}
