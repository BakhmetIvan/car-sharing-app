package com.carsharingapp.repository.rental;

import com.carsharingapp.model.Rental;
import com.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    private static final String INCORRECT_PARAM_VALUE_EXCEPTION =
            "Param should contains true or false, but contains: %s";
    private static final String IS_ACTIVE_FIELD_NAME = "actualReturnDate";

    @Override
    public String getKey() {
        return IS_ACTIVE_FIELD_NAME;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> {
            if (param.equals("true")) {
                return criteriaBuilder.isNull(root.get("actualReturnDate"));
            } else if (param.equals("false")) {
                return criteriaBuilder.isNotNull(root.get("actualReturnDate"));
            } else {
                throw new IllegalArgumentException(
                        String.format(INCORRECT_PARAM_VALUE_EXCEPTION, param)
                );
            }
        };
    }
}
