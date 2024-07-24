package mate.capsharingapp.repository.rental;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import mate.capsharingapp.model.Rental;
import mate.capsharingapp.repository.SpecificationProvider;
import mate.capsharingapp.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationProviderManager
        implements SpecificationProviderManager<Rental, Object> {
    private static final String SPECIFICATION_NOT_FOUND_EXCEPTION =
            "Can't find specification for key %s";
    private final List<SpecificationProvider<Rental, Object>> rentalSpecificationProviderList;

    @Override
    public SpecificationProvider<Rental, Object> getSpecificationProvider(String key) {
        return rentalSpecificationProviderList.stream()
                .filter(specProvider -> specProvider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format(SPECIFICATION_NOT_FOUND_EXCEPTION, key))
                );
    }
}
