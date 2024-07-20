package mate.capsharingapp.service;

import mate.capsharingapp.dto.user.UserRegistrationDto;
import mate.capsharingapp.dto.user.UserResponseDto;
import mate.capsharingapp.dto.user.UserUpdateDto;
import mate.capsharingapp.dto.user.UserUpdateRoleDto;
import mate.capsharingapp.exception.RegistrationException;
import mate.capsharingapp.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationDto registrationDto) throws RegistrationException;

    UserResponseDto getProfileInfo(User user);

    UserResponseDto updateProfileInfo(User user, UserUpdateDto updateDto);

    UserResponseDto updateRole(Long id, UserUpdateRoleDto updateRoleDto);
}
