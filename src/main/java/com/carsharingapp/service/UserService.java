package com.carsharingapp.service;

import com.carsharingapp.dto.user.UserRegistrationDto;
import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.dto.user.UserUpdateDto;
import com.carsharingapp.dto.user.UserUpdateRoleDto;
import com.carsharingapp.exception.RegistrationException;
import com.carsharingapp.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationDto registrationDto) throws RegistrationException;

    UserResponseDto getProfileInfo(User user);

    UserResponseDto updateProfileInfo(User user, UserUpdateDto updateDto);

    UserResponseDto updateRole(Long id, UserUpdateRoleDto updateRoleDto);
}
