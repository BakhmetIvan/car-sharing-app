package mate.capsharingapp.repository;

public interface SpecificationProviderManager<T, Y> {
    SpecificationProvider<T, Y> getSpecificationProvider(String key);
}
