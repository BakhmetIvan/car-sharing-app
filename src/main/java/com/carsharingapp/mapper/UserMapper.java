package com.carsharingapp.mapper;

import com.carsharingapp.config.MapperConfig;
import com.carsharingapp.dto.user.UserRegistrationDto;
import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.dto.user.UserUpdateDto;
import com.carsharingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationDto registrationDto);

    void userUpdateFromDto(@MappingTarget User user, UserUpdateDto updateDto);
}
