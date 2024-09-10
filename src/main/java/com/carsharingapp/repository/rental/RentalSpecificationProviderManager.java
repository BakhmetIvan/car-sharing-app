package com.carsharingapp.repository.rental;

import com.carsharingapp.model.Rental;
import com.carsharingapp.repository.SpecificationProvider;
import com.carsharingapp.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationProviderManager
        implements SpecificationProviderManager<Rental> {
    private static final String SPECIFICATION_NOT_FOUND_EXCEPTION =
            "Can't find specification for key %s";
    private final List<SpecificationProvider<Rental>> rentalSpecificationProviderList;

    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
        return rentalSpecificationProviderList.stream()
                .filter(specProvider -> specProvider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format(SPECIFICATION_NOT_FOUND_EXCEPTION, key))
                );
    }
}
