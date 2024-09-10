package com.carsharingapp.service.impl;

import com.carsharingapp.dto.user.UserRegistrationDto;
import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.dto.user.UserUpdateDto;
import com.carsharingapp.dto.user.UserUpdateRoleDto;
import com.carsharingapp.exception.EntityNotFoundException;
import com.carsharingapp.exception.ProfileException;
import com.carsharingapp.exception.RegistrationException;
import com.carsharingapp.mapper.UserMapper;
import com.carsharingapp.messages.ExceptionMessages;
import com.carsharingapp.model.Role;
import com.carsharingapp.model.User;
import com.carsharingapp.repository.RoleRepository;
import com.carsharingapp.repository.UserRepository;
import com.carsharingapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationDto registrationDto)
            throws RegistrationException {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RegistrationException(String.format(
                    ExceptionMessages.EMAIL_EXIST_EXCEPTION, registrationDto.getEmail())
            );
        }
        User user = userMapper.toModel(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles(roleRepository.findByName(Role.RoleName.ROLE_USER));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getProfileInfo(User user) {
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto updateProfileInfo(User user, UserUpdateDto updateDto) {
        if (updateDto.getEmail().equals(user.getEmail())
                || userRepository.findByEmail(updateDto.getEmail()).isPresent()) {
            throw new ProfileException(
                    String.format(ExceptionMessages.EMAIL_EXIST_EXCEPTION, updateDto.getEmail())
            );
        }
        userMapper.userUpdateFromDto(user, updateDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto updateRole(Long id, UserUpdateRoleDto updateRoleDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND_USER_EXCEPTION, id)
                )
        );
        Set<Role> roles = roleRepository.findByName(
                Role.RoleName.valueOf(updateRoleDto.getRole())
        );
        user.setRoles(roles);
        return userMapper.toDto(userRepository.save(user));
    }
}
