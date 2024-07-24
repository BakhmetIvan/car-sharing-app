package mate.capsharingapp.mapper;

import mate.capsharingapp.config.MapperConfig;
import mate.capsharingapp.dto.rental.RentalFullResponseDto;
import mate.capsharingapp.dto.rental.RentalRequestDto;
import mate.capsharingapp.dto.rental.RentalResponseDto;
import mate.capsharingapp.dto.rental.RentalSetActualReturnDateDto;
import mate.capsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    Rental toModel(RentalRequestDto requestDto);

    RentalResponseDto toDto(Rental rental);

    RentalFullResponseDto toFullDto(Rental rental);

    void setActualReturnDateFromDto(@MappingTarget Rental rental,
                                    RentalSetActualReturnDateDto returnDateDto);
}
