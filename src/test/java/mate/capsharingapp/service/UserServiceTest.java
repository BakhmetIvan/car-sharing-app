package mate.capsharingapp.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.capsharingapp.dto.user.UserResponseDto;
import mate.capsharingapp.dto.user.UserUpdateRoleDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.mapper.UserMapper;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.RoleRepository;
import mate.capsharingapp.repository.UserRepository;
import mate.capsharingapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final String NOT_FOUND_USER_EXCEPTION = "Can't find user by id: %d";
    private static final Role USER_ROLE = new Role().setName(Role.RoleName.ROLE_USER);
    private static final User USER = new User()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com")
            .setPassword("password")
            .setRoles(new HashSet<>(Collections.singleton(USER_ROLE)));
    private static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto()
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com");

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Get profile info by valid id should return valid UserResponseDto")
    public void getProfileInfo_ByValidId_ShouldReturnValidUserResponseDto() {
        when(userMapper.toDto(USER)).thenReturn(USER_RESPONSE_DTO);
        UserResponseDto actual = userService.getProfileInfo(USER);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(USER_RESPONSE_DTO, actual);
        verify(userMapper, times(1)).toDto(USER);
    }

    @Test
    @DisplayName("Update user role by valid id should return updated UserResponseDto")
    public void updateRole_ByValidId_ShouldReturnUpdatedUserResponseDto() {
        Long id = 1L;
        User updatedUser =
                USER.setRoles(Set.of(new Role().setName(Role.RoleName.ROLE_MANAGER)));
        UserUpdateRoleDto updateRoleDto = new UserUpdateRoleDto().setRole("ROLE_MANAGER");
        UserResponseDto updatedUserResponseDto = USER_RESPONSE_DTO;

        when(userRepository.findById(id)).thenReturn(Optional.of(USER));
        when(roleRepository.findByName(Role.RoleName.ROLE_MANAGER))
                .thenReturn(Set.of(new Role().setName(Role.RoleName.ROLE_MANAGER)));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedUserResponseDto);
        UserResponseDto actual = userService.updateRole(id, updateRoleDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(updatedUserResponseDto, actual);
        verify(userRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_MANAGER);
        verify(userRepository, times(1)).save(updatedUser);
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    @DisplayName("Update user role by invalid id should throw EntityNotFoundException")
    public void updateRole_ByInvalidId_ShouldThrowEntityNotFoundException() {
        Long id = 100L;
        UserUpdateRoleDto updateRoleDto = new UserUpdateRoleDto().setRole("ROLE_MANAGER");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        String actual = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.updateRole(id, updateRoleDto)).getMessage();
        String expected = String.format(NOT_FOUND_USER_EXCEPTION, id);

        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(id);
    }
}
