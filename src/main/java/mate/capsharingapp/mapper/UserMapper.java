package mate.capsharingapp.mapper;

import mate.capsharingapp.config.MapperConfig;
import mate.capsharingapp.dto.user.UserRegistrationDto;
import mate.capsharingapp.dto.user.UserResponseDto;
import mate.capsharingapp.dto.user.UserUpdateDto;
import mate.capsharingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationDto registrationDto);

    void userUpdateFromDto(@MappingTarget User user, UserUpdateDto updateDto);
}
